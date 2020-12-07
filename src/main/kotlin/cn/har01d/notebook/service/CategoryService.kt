package cn.har01d.notebook.service

import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.entity.CategoryRepository
import cn.har01d.notebook.service.IdUtils.CATEGORY_OFFSET
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CategoryService(private val repository: CategoryRepository) {

    fun list(pageable: Pageable) = repository.findAll(pageable)

    fun create(dto: Category): Category {
        if (repository.existsByName(dto.name)) {
            throw AppException("分类已经存在")
        }
        val category = Category(dto.name, dto.description)
        return repository.save(category)
    }

    fun update(id: String, dto: Category): Category {
        val other = repository.findByName(dto.name)
        val category = repository.findByIdOrNull(decode(id)) ?: throw AppNotFoundException("分类不存在")
        if (other != null && other.id != category.id) {
            throw AppException("分类已经存在")
        }
        category.name = dto.name
        category.description = dto.description
        return repository.save(category)
    }

    fun delete(id: String) {
        val category = repository.findByIdOrNull(decode(id)) ?: return
        repository.delete(category)
    }

    private fun decode(id: String) = IdUtils.decode(id) - CATEGORY_OFFSET
}
