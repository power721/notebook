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
import cn.har01d.notebook.service.IdUtils.NOTEBOOK_OFFSET
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NotebookService(
        private val notebookRepository: NotebookRepository,
        private val noteRepository: NoteRepository,
        private val userService: UserService
) {
    fun list(q: String?, pageable: Pageable): Page<Notebook> {
        return if (q != null) {
            notebookRepository.findByNameContains(q, pageable)
        } else {
            notebookRepository.findAll(pageable)
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

    fun getUserNotebooks(userId: Int, pageable: Pageable): Page<Notebook> {
        val user = userService.requireUser(userId)
        return notebookRepository.findByOwner(user, pageable)
    }

    fun get(id: String): Notebook {
        return notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("笔记本不存在")
    }

    fun create(dto: NotebookDto): Notebook {
        if (dto.name.isBlank()) {
            throw AppException("笔记本名字不能为空")
        }
        val user = userService.requireCurrentUser()
        if (notebookRepository.existsByOwnerAndName(user, dto.name)) {
            throw AppException("笔记本名字已经存在")
        }
        val notebook = Notebook(dto.name, dto.description, user)
        return notebookRepository.save(notebook)
    }

    fun update(id: String, dto: NotebookDto): Notebook {
        if (dto.name.isBlank()) {
            throw AppException("笔记本名字不能为空")
        }
        val user = userService.requireCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("笔记本不存在")
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        val other = notebookRepository.findByOwnerAndName(user, dto.name)
        if (other != null && other.id != notebook.id) {
            throw AppException("笔记本名字已经存在")
        }
        notebook.name = dto.name
        notebook.description = dto.description
        return notebookRepository.save(notebook)
    }

    fun delete(id: String) {
        val user = userService.requireCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: return
        if (notebook.owner.id != user.id) {
            throw AppForbiddenException("用户无权操作")
        }
        notebookRepository.delete(notebook)
    }

    fun getNotes(id: String, pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        val notebook = notebookRepository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("笔记本不存在")
        return if (user != null && notebook.owner.id == user.id) {
            noteRepository.findByNotebook(notebook, pageable)
        } else {
            noteRepository.findByNotebookAndAccess(notebook, Access.PUBLIC, pageable)
        }
    }

    private fun decode(id: String) = IdUtils.decode(id) - NOTEBOOK_OFFSET

}