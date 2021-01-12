package cn.har01d.notebook.controller

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.dto.NotebookDto
import cn.har01d.notebook.service.NotebookService
import cn.har01d.notebook.vo.toVo
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RequestMapping("/notebooks")
@RestController
class NotebookController(private val service: NotebookService) {
    @GetMapping
    fun list(q: String?, pageable: Pageable) = service.list(q, pageable).map { it.toVo() }

    @GetMapping("{id}")
    fun get(@PathVariable id: String) = service.get(id).toVo()

    @Transactional
    @GetMapping("{id}/notes")
    fun getNotes(@PathVariable id: String, pageable: Pageable) = service.getNotes(id, pageable).map { it.toVo() }

    @PostMapping
    fun create(@RequestBody dto: NotebookDto) = service.create(dto).toVo()

    @PutMapping("{id}")
    fun update(@PathVariable id: String, @RequestBody dto: NotebookDto) = service.update(id, dto).toVo()

    @PostMapping("{id}/access")
    fun updateAccess(@PathVariable id: String, access: Access) = service.updateAccess(id, access)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: String, force: Boolean) = service.delete(id, force)
}
