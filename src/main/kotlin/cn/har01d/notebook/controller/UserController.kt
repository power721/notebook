package cn.har01d.notebook.controller

import cn.har01d.notebook.service.NoteService
import cn.har01d.notebook.service.NotebookService
import cn.har01d.notebook.vo.toVo
import cn.har01d.notebook.vo.toVo2
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users")
@RestController
class UserController(
        private val notebookService: NotebookService,
        private val noteService: NoteService
) {

    @GetMapping("/{id}/notes")
    fun getUserNotes(@PathVariable id: Int, pageable: Pageable) = noteService.getUserNotes(id, pageable).map { it.toVo2() }

    @GetMapping("/-/notes")
    fun getMyNotes(pageable: Pageable) = noteService.getMyNotes(pageable).map { it.toVo2() }

    @GetMapping("/{id}/notebooks")
    fun getUserNotebooks(@PathVariable id: Int, pageable: Pageable) = notebookService.getUserNotebooks(id, pageable).map { it.toVo() }

    @GetMapping("/-/notebooks")
    fun getMyNotebooks(q: String?, pageable: Pageable) = notebookService.getMyNotebooks(q, pageable).map { it.toVo() }

}