package cn.har01d.notebook.service

import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.dto.CategoryDto
import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.entity.CategoryRepository
import cn.har01d.notebook.entity.Note
import cn.har01d.notebook.entity.NoteRepository
import cn.har01d.notebook.service.IdUtils.CATEGORY_OFFSET
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoryService(
        private val repository: CategoryRepository,
        private val noteRepository: NoteRepository,
        private val userService: UserService,
        private val configService: ConfigService,
        private val auditService: AuditService,
) {

    fun list(q: String?, pageable: Pageable): Page<Category> {
        return if (q != null) {
            repository.findByNameContains(q, pageable)
        } else {
            repository.findAll(pageable)
        }
    }

    fun get(id: String): Category {
        return repository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("分类不存在")
    }

    fun getNotes(id: String, pageable: Pageable): Page<Note> {
        val user = userService.getCurrentUser()
        val category = repository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("分类不存在")
        if (user != null) {
            return noteRepository.findByCategoryAndPublicOrOwn(category, user, pageable)
        }
        return noteRepository.findByCategoryAndPublic(category, pageable)
    }

    fun create(dto: CategoryDto): Category {
        if (repository.existsByName(dto.name)) {
            throw AppException("分类已经存在")
        }

        val limit = configService.get("categories_limit", 20)
        if (repository.count() >= limit) {
            throw AppException("分类数量不能超过$limit")
        }

        val category = Category(dto.name, dto.description)
        return repository.save(category).also { auditService.auditCategoryCreate(userService.requireCurrentUser(), it) }
    }

    fun update(id: String, dto: CategoryDto): Category {
        val other = repository.findByName(dto.name)
        val category = repository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("分类不存在")
        if (other != null && other.id != category.id) {
            throw AppException("分类已经存在")
        }
        category.name = dto.name
        category.description = dto.description
        return repository.save(category).also { auditService.auditCategoryUpdate(userService.requireCurrentUser(), it) }
    }

    fun delete(id: String) {
        val category = repository.findByIdOrNull(decode(id)) ?: return
        repository.delete(category)
        auditService.auditCategoryDelete(userService.requireCurrentUser(), category)
    }

    private fun decode(id: String) = IdUtils.decode(id) - CATEGORY_OFFSET
}
