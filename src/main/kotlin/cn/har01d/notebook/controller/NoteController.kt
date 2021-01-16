package cn.har01d.notebook.controller

import cn.har01d.notebook.dto.NoteDto
import cn.har01d.notebook.service.NoteService
import cn.har01d.notebook.vo.toVo
import cn.har01d.notebook.vo.toVo2
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RequestMapping("/notes")
@RestController
class NoteController(private val service: NoteService) {
    @GetMapping
    fun list(q: String?, pageable: Pageable) = service.list(q, pageable).map { it.toVo2() }

    @PostMapping
    fun create(@RequestBody dto: NoteDto) = service.create(dto).toVo()

    @Transactional
    @GetMapping("{id}")
    fun get(@PathVariable id: String, view: Boolean) = service.get(id).also {
        if (view) service.updateViews(it)
    }.toVo()

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: String) = service.delete(id)

    @GetMapping("{id}/history")
    fun getNoteHistory(@PathVariable id: String) = service.getNoteHistory(id).map { it.toVo() }

    @GetMapping("{id}/content/{version}")
    fun getNoteContent(@PathVariable id: String, @PathVariable version: Int) = service.getNoteContent(id, version)

    @Transactional
    @PostMapping("{id}/content/{version}")
    fun revertNoteContent(@PathVariable id: String, @PathVariable version: Int) = service.revertNoteContent(id, version).toVo()

    @Transactional
    @DeleteMapping("{id}/content/{version}")
    fun deleteNoteContent(@PathVariable id: String, @PathVariable version: Int) = service.deleteNoteContent(id, version)

    @Transactional
    @PostMapping("{id}/move")
    fun move(@PathVariable id: String, notebookId: String) = service.move(id, notebookId).toVo()

    @Transactional
    @PostMapping("{id}/revert")
    fun revert(@PathVariable id: String) = service.revert(id).toVo()

    @PutMapping("{id}")
    fun update(@PathVariable id: String, @RequestBody dto: NoteDto) = service.update(id, dto).toVo()
}
