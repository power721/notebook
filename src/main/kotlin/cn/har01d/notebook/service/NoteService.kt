package cn.har01d.notebook.service

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.Error
import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.dto.CommentDto
import cn.har01d.notebook.dto.NoteDto
import cn.har01d.notebook.dto.TagDto
import cn.har01d.notebook.entity.*
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.IdUtils.CATEGORY_OFFSET
import cn.har01d.notebook.util.IdUtils.NOTEBOOK_OFFSET
import cn.har01d.notebook.util.wordCount
import cn.har01d.notebook.vo.NoteStats
import com.github.benmanes.caffeine.cache.Cache
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val commentRepository: CommentRepository,
    private val notebookRepository: NotebookRepository,
    private val contentRepository: NoteContentRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val userService: UserService,
    private val auditService: AuditService,
    private val configService: ConfigService,
    private val cacheService: CacheService,
) {
    private val viewCache: Cache<String, Boolean> = cacheService.boolCache("Note", Duration.ofDays(1))

    fun list(q: String?, pageable: Pageable): Page<Note> {
        if (q != null && q.isNotEmpty()) {
            return search(q, pageable)
        }

        val user = userService.getCurrentUser()
        return if (user == null) {
            noteRepository.findPublic(pageable)
        } else {
            noteRepository.findPublicOrOwn(user, pageable)
        }
    }

    fun search(q: String, pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        return if (user == null) {
            noteRepository.searchPublic(q, pageable)
        } else {
            noteRepository.searchPublicOrOwn(q, user, pageable)
        }
    }

    fun getMyNotes(pageable: Pageable): Page<Note> {
        val user = userService.requireCurrentUser()
        return noteRepository.findByAuthor(user, pageable)
    }

    fun getTrashNotes(pageable: Pageable): Page<Note> {
        val user = userService.requireCurrentUser()
        return noteRepository.findByAuthorAndDeleted(user, pageable)
    }

    fun getUserNotes(userId: String, pageable: Pageable): Page<Note> {
        val user = userService.requireUser(userId)
        return noteRepository.findPublicAndAuthor(user, pageable)
    }

    fun get(id: String): Note {
        val note = noteRepository.findByRid(id) ?: noteRepository.findBySlug(id) ?: throw AppNotFoundException("???????????????")
        val user = userService.getCurrentUser()
        if (note.deleted) {
            if (user == null || note.author.id != user.id) {
                throw AppNotFoundException("???????????????")
            }
        }

        if (note.access == Access.PRIVATE) {
            if (user == null || note.author.id != user.id) {
                throw AppForbiddenException("??????????????????")
            }
        }

        if (configService.get(Const.SHOW_WORDS, true)) {
            val text = Jsoup.parse(note.content!!.content).text()
            note.words = wordCount(text)
        }

        return note
    }

    fun getNote(id: String): Note {
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("???????????????")
        if (note.deleted) {
            throw AppNotFoundException("???????????????")
        }
        return note
    }

    fun updateViews(note: Note, request: HttpServletRequest) {
        if (note.access == Access.PRIVATE || note.deleted) {
            return
        }
        val key = note.id.toString() + "-" + request.remoteAddr + "-" + LocalDate.now()
        if (viewCache.getIfPresent(key) != null) {
            return
        }
        val user = userService.getCurrentUser()
        if (user != null && note.author.id == user.id) {
            return
        }
        viewCache.put(key, true)
        noteRepository.updateViews(note.id!!)
    }

    private val regex = "\\[/notes/(.+)]".toRegex()
    fun String.notelink() = this.replace(regex) {
        val nid = it.groupValues[1]
        val n = noteRepository.findByRid(nid) ?: noteRepository.findBySlug(nid)
        if (n != null) "<a href=\"/#/notes/$nid\" target=\"_blank\">${n.content!!.title}</a>" else it.value
    }

    fun create(dto: NoteDto): Note {
        if (dto.title.isBlank()) {
            throw AppException("????????????????????????")
        }
        val user = userService.requireCurrentUser()
        val notebook = getNotebook(dto.notebookId ?: throw AppException("?????????ID??????"))
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        if (dto.slug != null && dto.slug.isNotEmpty()) {
            if (noteRepository.existsBySlug(dto.slug)) {
                throw AppException("slug??????")
            }
            if (noteRepository.existsByRid(dto.slug)) {
                throw AppException("slug???id??????")
            }
        }
        val category = getCategory(dto.categoryId ?: throw AppException("??????ID??????"))
        var rid = IdUtils.generate()
        while (noteRepository.existsByRid(rid)) {
            rid = IdUtils.generate()
        }

        if (dto.access != null) {
            if (notebook.access == Access.SECRET && dto.access == Access.PUBLIC) {
                throw AppException("???????????????????????????")
            }
            if (notebook.access == Access.PRIVATE && dto.access != Access.PRIVATE) {
                throw AppException("???????????????????????????")
            }
        }

        val note = noteRepository.save(
            Note(
                user, notebook, category, getTags(dto.tags), access = dto.access
                    ?: notebook.access, rid = rid
            )
        )
        if (dto.slug != null && dto.slug.isNotEmpty()) {
            note.slug = dto.slug
        }
        val newcontent = dto.content.notelink()
        val content = contentRepository.save(NoteContent(dto.title, newcontent, note, dto.markdown))
        note.content = content
        notebook.updatedTime = Instant.now()
        notebookRepository.save(notebook)
        return noteRepository.save(note).also { auditService.auditNoteCreate(user, note) }
    }

    fun update(id: String, dto: NoteDto): Note {
        if (dto.title.isBlank()) {
            throw AppException("????????????????????????")
        }
        val user = userService.requireCurrentUser()

        val note = getNote(id)
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        if (dto.slug != null && dto.slug.isNotEmpty()) {
            val n = noteRepository.findBySlug(dto.slug)
            if (n != null && n.id != note.id) {
                throw AppException("slug??????")
            }
            if (noteRepository.existsByRid(dto.slug)) {
                throw AppException("slug???id??????")
            }
            note.slug = dto.slug
        }

        if (dto.tags != null) {
            note.tags = getTags(dto.tags)
        }

        if (dto.notebookId != null) {
            val notebook = getNotebook(dto.notebookId)
            if (notebook.owner.id != user.id) {
                throw AppForbiddenException("??????????????????")
            }
            note.notebook = notebook
        }

        if (dto.categoryId != null) {
            note.category = getCategory(dto.categoryId)
        }

        if (dto.access != null) {
            if (note.notebook.access == Access.SECRET && dto.access == Access.PUBLIC) {
                throw AppException("???????????????????????????")
            }
            if (note.notebook.access == Access.PRIVATE && dto.access != Access.PRIVATE) {
                throw AppException("???????????????????????????")
            }
            note.access = dto.access
        }

        val content = note.content!!
        val newcontent = dto.content.notelink()
        if (content.title != dto.title || content.content != newcontent) {
            val version = contentRepository.version(note) + 1
            note.content = contentRepository.save(NoteContent(dto.title, newcontent, note, dto.markdown, version))
        }
        note.updatedTime = Instant.now()
        note.notebook.updatedTime = Instant.now()
        notebookRepository.save(note.notebook)
        return noteRepository.save(note).also { auditService.auditNoteUpdate(user, note) }
    }

    fun move(id: String, notebookId: String): Note {
        val user = userService.requireCurrentUser()
        val note = getNote(id)
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        val notebook = getNotebook(notebookId)
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        if (notebook.id == note.notebook.id) {
            return note
        }

        if (notebook.access == Access.PRIVATE) {
            note.access = Access.PRIVATE
        } else if (notebook.access == Access.SECRET && note.access == Access.PUBLIC) {
            note.access = Access.SECRET
        }

        note.notebook = notebook
        note.updatedTime = Instant.now()
        note.notebook.updatedTime = Instant.now()
        notebookRepository.save(note.notebook)
        return noteRepository.save(note)
            .also { auditService.auditNoteUpdate(user, note, "???????????????${note.content?.title}???${notebook.name}") }
    }

    fun getNoteHistory(id: String): List<NoteContent> {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: noteRepository.findBySlug(id) ?: throw AppNotFoundException("???????????????")
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        return contentRepository.findByNoteOrderByIdDesc(note)
    }

    fun getNoteContent(id: String, version: Int): NoteContent {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("???????????????")
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        return contentRepository.findByNoteAndVersion(note, version) ?: throw AppNotFoundException("???????????????")
    }

    fun revertNoteContent(id: String, version: Int): Note {
        val user = userService.requireCurrentUser()
        val note = getNote(id)
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        val content = contentRepository.findByNoteAndVersion(note, version) ?: throw AppNotFoundException("???????????????")
        note.content = content
        note.updatedTime = Instant.now()
        return noteRepository.save(note)
            .also { auditService.auditNoteUpdate(user, note, "???????????????${note.content?.title}??????$version:") }
    }

    @Transactional
    fun deleteNoteContent(id: String, version: Int) {
        val user = userService.requireCurrentUser()
        val note = getNote(id)
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        contentRepository.deleteByNoteAndVersion(note, version)
        auditService.auditNoteContentDelete(user, note, version)
    }

    @Transactional
    fun delete(id: String) {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: return
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        if (note.deleted) {
            contentRepository.deleteAllByNote(note)
            noteRepository.delete(note)
            auditService.auditNoteDelete(user, note)
        } else {
            note.deleted = true
            noteRepository.save(note)
            auditService.auditNoteDelete2(user, note)
        }
    }

    fun revert(id: String): Note {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("???????????????")
        if (note.author.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        note.deleted = false
        return noteRepository.save(note)
            .also { auditService.auditNoteUpdate(user, note, "???????????????${note.content?.title}") }
    }

    @Transactional
    fun cleanTrash() {
        val user = userService.requireCurrentUser()
        contentRepository.deleteAllByNoteAuthorAndNoteDeletedTrue(user)
        noteRepository.deleteAllByAuthorAndDeletedTrue(user)
        auditService.auditCleanTrash(user)
    }

    fun stats() = NoteStats(
        noteRepository.countByDeleted(false),
        noteRepository.countByDeleted(true),
        noteRepository.views(),
        noteRepository.countByAccess(Access.PUBLIC),
        noteRepository.countByAccess(Access.SECRET),
        noteRepository.countByAccess(Access.PRIVATE),
    )

    fun getComments(id: String, pageable: Pageable): Page<Comment> {
        if (!configService.get(Const.ENABLE_COMMENT, false)) {
            throw AppForbiddenException("?????????????????????", Error.COMMENT_DISABLED)
        }

        val note = noteRepository.findByRid(id) ?: noteRepository.findBySlug(id) ?: throw AppNotFoundException("???????????????")
        return commentRepository.findByNote(note, pageable)
    }

    fun getStickyComments(id: String, sort: Sort): List<Comment> {
        if (!configService.get(Const.ENABLE_COMMENT, false)) {
            throw AppForbiddenException("?????????????????????", Error.COMMENT_DISABLED)
        }

        val note = noteRepository.findByRid(id) ?: noteRepository.findBySlug(id) ?: throw AppNotFoundException("???????????????")
        return commentRepository.findByNoteAndStickyTrue(note, sort)
    }

    private val safelist = Safelist.simpleText().addTags("code", "del", "sub", "sup")

    fun addComment(id: String, dto: CommentDto): Comment {
        if (!configService.get(Const.ENABLE_COMMENT, false)) {
            throw AppForbiddenException("?????????????????????", Error.COMMENT_DISABLED)
        }

        val content = Jsoup.clean(dto.content, safelist).notelink()
        if (content.length > 2048) {
            throw AppException("??????????????????", Error.COMMENT_TOO_LONG)
        }
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: noteRepository.findBySlug(id) ?: throw AppNotFoundException("???????????????")
        val comment = Comment(user, note, content)
        return commentRepository.save(comment)
    }

    private fun getTags(tags: List<TagDto>?): List<Tag> {
        if (tags == null) {
            return ArrayList()
        }
        return tags.map { tag ->
            tagRepository.findByName(tag.name) ?: tagRepository.save(Tag(tag.name))
        }
    }

    private fun getNotebook(id: String) = notebookRepository.findByIdOrNull(IdUtils.decode(id) - NOTEBOOK_OFFSET)
        ?: throw AppNotFoundException("??????????????????")

    private fun getCategory(id: String) = categoryRepository.findByIdOrNull(IdUtils.decode(id) - CATEGORY_OFFSET)
        ?: throw AppNotFoundException("???????????????")
}
