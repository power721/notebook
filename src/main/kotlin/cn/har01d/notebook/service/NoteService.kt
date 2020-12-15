package cn.har01d.notebook.service

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.dto.NoteDto
import cn.har01d.notebook.entity.*
import cn.har01d.notebook.service.IdUtils.CATEGORY_OFFSET
import cn.har01d.notebook.service.IdUtils.NOTEBOOK_OFFSET
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import javax.transaction.Transactional

@Service
class NoteService(
        private val noteRepository: NoteRepository,
        private val notebookRepository: NotebookRepository,
        private val contentRepository: NoteContentRepository,
        private val categoryRepository: CategoryRepository,
        private val userService: UserService
) {
    fun list(pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        return if (user == null) {
            noteRepository.findByAccess(Access.PUBLIC, pageable)
        } else {
            noteRepository.findByAccessOrAuthor(Access.PUBLIC, user, pageable)
        }
    }

    fun getMyNotes(pageable: Pageable): Page<Note> {
        val user = userService.requireCurrentUser()
        return noteRepository.findByAuthor(user, pageable)
    }

    fun getUserNotes(userId: Int, pageable: Pageable): Page<Note> {
        val user = userService.requireUser(userId)
        return noteRepository.findByAccessOrAuthor(Access.PUBLIC, user, pageable)
    }

    fun get(id: String): Note {
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.access == Access.PRIVATE) {
            val user = userService.getCurrentUser()
            if (user == null || note.author.id != user.id) {
                throw AppForbiddenException("用户无权操作")
            }
        }
        return note
    }

    fun updateViews(note: Note) {
        // TODO: check IP
        if (note.access == Access.PRIVATE) {
            return
        }
        note.views = note.views + 1
        noteRepository.save(note)
    }

    fun create(dto: NoteDto): Note {
        if (dto.title.isBlank()) {
            throw AppException("笔记标题不能为空")
        }
        val user = userService.requireCurrentUser()
        val notebook = getNotebook(dto.notebookId ?: throw AppException("笔记本ID缺失"))
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        val category = getCategory(dto.categoryId ?: throw AppException("分类ID缺失"))
        var rid = IdUtils.generate()
        while (noteRepository.existsByRid(rid)) {
            rid = IdUtils.generate()
        }

        val note = noteRepository.save(Note(user, notebook, category, access = dto.access ?: Access.PUBLIC, rid = rid))
        val content = contentRepository.save(NoteContent(dto.title, dto.content, note, dto.markdown))
        note.content = content
        notebook.updatedTime = Instant.now()
        notebookRepository.save(notebook)
        return noteRepository.save(note)
    }

    fun update(id: String, dto: NoteDto): Note {
        if (dto.title.isBlank()) {
            throw AppException("笔记标题不能为空")
        }
        val user = userService.requireCurrentUser()

        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }

        if (dto.notebookId != null) {
            val notebook = getNotebook(dto.notebookId)
            if (notebook.owner.id != user.id) {
                throw AppForbiddenException("用户无权操作")
            }
            note.notebook = notebook
        }

        if (dto.categoryId != null) {
            note.category = getCategory(dto.categoryId)
        }

        if (dto.access != null) {
            note.access = dto.access
        }

        val content = note.content!!
        if (content.title != dto.title || content.content != dto.content) {
            note.content = contentRepository.save(NoteContent(dto.title, dto.content, note, dto.markdown, content.version + 1))
        }
        note.updatedTime = Instant.now()
        note.notebook.updatedTime = Instant.now()
        notebookRepository.save(note.notebook)
        return noteRepository.save(note)
    }

    fun move(id: String, notebookId: String): Note {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }

        val notebook = getNotebook(notebookId)
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }

        if (notebook.id == note.notebook.id) {
            return note
        }

        note.notebook = notebook
        note.updatedTime = Instant.now()
        note.notebook.updatedTime = Instant.now()
        notebookRepository.save(note.notebook)
        return noteRepository.save(note)
    }

    fun getNoteHistory(id: String): List<NoteContent> {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        return contentRepository.findByNoteOrderByIdDesc(note)
    }

    fun getNoteContent(id: String, version: Int): NoteContent {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        return contentRepository.findByNoteAndVersion(note, version) ?: throw AppNotFoundException("版本不存在")
    }

    fun revertNoteContent(id: String, version: Int): Note {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: throw AppNotFoundException("笔记不存在")
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        val content = contentRepository.findByNoteAndVersion(note, version) ?: throw AppNotFoundException("版本不存在")
        note.content = content
        note.updatedTime = Instant.now()
        return noteRepository.save(note)
    }

    @Transactional
    fun delete(id: String) {
        val user = userService.requireCurrentUser()
        val note = noteRepository.findByRid(id) ?: return
        if (note.author.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        contentRepository.deleteAllByNote(note)
        noteRepository.delete(note)
    }

    private fun getNotebook(id: String) = notebookRepository.findByIdOrNull(IdUtils.decode(id) - NOTEBOOK_OFFSET)
            ?: throw AppNotFoundException("笔记本不存在")

    private fun getCategory(id: String) = categoryRepository.findByIdOrNull(IdUtils.decode(id) - CATEGORY_OFFSET)
            ?: throw AppNotFoundException("分类不存在")
}
