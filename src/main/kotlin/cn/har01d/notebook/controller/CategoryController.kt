package cn.har01d.notebook.controller

import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.service.CategoryService
import cn.har01d.notebook.vo.toVo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RequestMapping("/categories")
@RestController
class CategoryController(private val service: CategoryService) {
    @GetMapping
    fun list(pageable: Pageable) = service.list(pageable).map { it.toVo() }

    @PostMapping
    fun create(@RequestBody dto: Category) = service.create(dto).toVo()

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody dto: Category) = service.update(id, dto).toVo()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(id)
}
