package cn.har01d.notebook.controller

import cn.har01d.notebook.dto.TagDto
import cn.har01d.notebook.service.TagService
import cn.har01d.notebook.vo.toVo2
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RequestMapping("/tags")
@RestController
class TagController(private val service: TagService) {
    @GetMapping
    fun list(q: String?, pageable: Pageable) = service.list(q, pageable)

    @Transactional
    @GetMapping("{tag}/notes")
    fun getNotes(@PathVariable tag: String, pageable: Pageable) = service.getNotes(tag, pageable).map { it.toVo2() }

    @PostMapping
    fun create(@RequestBody tag: TagDto) = service.create(tag)

    @DeleteMapping("/{tag}")
    fun delete(@PathVariable tag: String) = service.delete(tag)
}
