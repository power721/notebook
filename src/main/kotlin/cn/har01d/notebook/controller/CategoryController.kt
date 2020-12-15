package cn.har01d.notebook.controller

import cn.har01d.notebook.dto.CategoryDto
import cn.har01d.notebook.service.CategoryService
import cn.har01d.notebook.vo.toVo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RequestMapping("/categories")
@RestController
class CategoryController(private val service: CategoryService) {
    @GetMapping
    fun list(q: String?, pageable: Pageable) = service.list(q, pageable).map { it.toVo() }

    @GetMapping("{id}")
    fun get(@PathVariable id: String) = service.get(id).toVo()

    @GetMapping("{id}/notes")
    fun getNotes(@PathVariable id: String, pageable: Pageable) = service.getNotes(id, pageable).map { it.toVo() }

    @PostMapping
    fun create(@RequestBody dto: CategoryDto) = service.create(dto).toVo()

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody dto: CategoryDto) = service.update(id, dto).toVo()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) = service.delete(id)
}
