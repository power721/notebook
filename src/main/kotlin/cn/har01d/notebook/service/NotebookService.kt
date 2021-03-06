package cn.har01d.notebook.service

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.dto.NotebookDto
import cn.har01d.notebook.entity.Note
import cn.har01d.notebook.entity.NoteRepository
import cn.har01d.notebook.entity.Notebook
import cn.har01d.notebook.entity.NotebookRepository
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.IdUtils.NOTEBOOK_OFFSET
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import javax.transaction.Transactional

@Service
class NotebookService(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository,
    private val userService: UserService,
    private val configService: ConfigService,
    private val auditService: AuditService,
) {
    fun list(q: String?, pageable: Pageable): Page<Notebook> {
        val user = userService.getCurrentUser()
        if (user != null) {
            return if (q == null || q.isEmpty()) {
                notebookRepository.findPublicOrOwner(user, pageable)
            } else {
                notebookRepository.searchPublicOrOwner(q, user, pageable)
            }
        }
        return if (q != null && q.isNotEmpty()) {
            notebookRepository.findByNameContainsAndAccess(q, Access.PUBLIC, pageable)
        } else {
            notebookRepository.findByAccess(Access.PUBLIC, pageable)
        }
    }

    fun getMyNotebooks(q: String?, pageable: Pageable): Page<Notebook> {
        val user = userService.requireCurrentUser()
        return if (q != null) {
            notebookRepository.findByOwnerAndNameContains(user, q, pageable)
        } else {
            notebookRepository.findByOwner(user, pageable)
        }
    }

    fun getUserNotebooks(userId: String, pageable: Pageable): Page<Notebook> {
        val user = userService.requireUser(userId)
        return notebookRepository.findByOwnerAndAccess(user, Access.PUBLIC, pageable)
    }

    fun get(id: String): Notebook {
        val notebook = notebookRepository.findBySlug(id) ?: notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("??????????????????")
        if (notebook.access == Access.PRIVATE) {
            val user = userService.getCurrentUser()
            if (user == null || notebook.owner.id != user.id) {
                throw AppForbiddenException("??????????????????")
            }
        }
        return notebook
    }

    fun create(dto: NotebookDto): Notebook {
        if (dto.name.isBlank()) {
            throw AppException("???????????????????????????")
        }
        val user = userService.requireCurrentUser()
        if (notebookRepository.existsByOwnerAndName(user, dto.name)) {
            throw AppException("???????????????????????????")
        }
        val limit = configService.get("notebooks_limit", 20)
        if (notebookRepository.countByOwner(user) >= limit) {
            throw AppException("???????????????????????????$limit")
        }
        if (dto.slug != null && dto.slug.isNotEmpty() && notebookRepository.existsBySlug(dto.slug)) {
            throw AppException("slug??????")
        }
        val slug = if (dto.slug == "") null else dto.slug
        val notebook = Notebook(dto.name, dto.description, user, slug, dto.access ?: Access.PUBLIC)
        return notebookRepository.save(notebook).also { auditService.auditNotebookCreate(user, notebook) }
    }

    @Transactional
    fun update(id: String, dto: NotebookDto): Notebook {
        if (dto.name.isBlank()) {
            throw AppException("???????????????????????????")
        }
        val user = userService.requireCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("??????????????????")
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        if (dto.slug != null && dto.slug.isNotEmpty()) {
            val other = notebookRepository.findBySlug(dto.slug)
            if (other != null && other.id != notebook.id) {
                throw AppException("slug??????")
            }
        }
        val other = notebookRepository.findByOwnerAndName(user, dto.name)
        if (other != null && other.id != notebook.id) {
            throw AppException("???????????????????????????")
        }
        if (dto.access != null && notebook.access != dto.access) {
            if (notebook.access == Access.PUBLIC && dto.access == Access.SECRET) {
                noteRepository.updateSecretNotebook(notebook)
            }
            if (notebook.access != Access.PRIVATE && dto.access == Access.PRIVATE) {
                noteRepository.updatePrivateNotebook(notebook)
            }
            notebook.access = dto.access
        }
        notebook.name = dto.name
        notebook.slug = dto.slug
        notebook.description = dto.description
        notebook.updatedTime = Instant.now()
        return notebookRepository.save(notebook).also { auditService.auditNotebookUpdate(user, notebook) }
    }

    @Transactional
    fun updateAccess(id: String, access: Access) {
        val user = userService.requireCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("??????????????????")
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }
        if (notebook.access == Access.PRIVATE && access != Access.PRIVATE) {
            throw AppException("????????????")
        }
        if (notebook.access == Access.SECRET && access == Access.PUBLIC) {
            throw AppException("????????????")
        }
        noteRepository.updateNoteAccess(notebook, access)
    }

    @Transactional
    fun delete(id: String, force: Boolean = false) {
        val user = userService.requireCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: return
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("??????????????????")
        }

        if (force) {
            noteRepository.deleteAllByNotebook(notebook)
        } else {
            if (noteRepository.countByNotebook(notebook) > 0) {
                throw AppForbiddenException("??????????????????")
            }
        }

        notebookRepository.delete(notebook)
        auditService.auditNotebookDelete(user, notebook)
    }

    fun getNotes(id: String, pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("??????????????????")
        return if (user != null && notebook.owner.id == user.id) {
            noteRepository.findByNotebook(notebook, pageable)
        } else {
            if (notebook.access == Access.PRIVATE) {
                throw AppForbiddenException("??????????????????")
            } else {
                noteRepository.findByNotebookAndAccess(notebook, notebook.access, pageable)
            }
        }
    }

    private fun decode(id: String) = IdUtils.decode(id) - NOTEBOOK_OFFSET

}
