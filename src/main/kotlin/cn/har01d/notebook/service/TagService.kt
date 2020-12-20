package cn.har01d.notebook.service

import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.dto.TagDto
import cn.har01d.notebook.entity.Note
import cn.har01d.notebook.entity.NoteRepository
import cn.har01d.notebook.entity.Tag
import cn.har01d.notebook.entity.TagRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TagService(
        private val repository: TagRepository,
        private val noteRepository: NoteRepository,
        private val userService: UserService,
        private val auditService: AuditService,
) {
    fun list(q: String?, pageable: Pageable): Page<Tag> {
        return if (q != null) {
            repository.findByNameContains(q, pageable)
        } else {
            repository.findAll(pageable)
        }
    }

    fun getNotes(name: String, pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        val tag = repository.findByName(name) ?: throw AppNotFoundException("标签不存在")
        return if (user != null) {
            noteRepository.findByTagAndPublicOrOwn(tag, user, pageable)
        } else {
            noteRepository.findByTagAndPublic(tag, pageable)
        }
    }

    fun create(tag: TagDto) = repository.save(Tag(tag.name)).also { auditService.auditTagCreate(userService.requireCurrentUser(), it) }

    fun delete(name: String) {
        val tag = repository.findByName(name) ?: return
        val count = noteRepository.countByTagsContains(tag)
        if (count == 0L) {
            repository.delete(tag)
        } else {
            throw AppException("存在笔记包含此标签")
        }
    }
}
