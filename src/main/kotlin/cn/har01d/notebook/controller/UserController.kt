package cn.har01d.notebook.controller

import cn.har01d.notebook.dto.UserInfoDto
import cn.har01d.notebook.service.NoteService
import cn.har01d.notebook.service.NotebookService
import cn.har01d.notebook.service.UserService
import cn.har01d.notebook.vo.toVo
import cn.har01d.notebook.vo.toVo2
import cn.har01d.notebook.vo.toVo3
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RequestMapping("/users")
@RestController
class UserController(
    private val userService: UserService,
    private val notebookService: NotebookService,
    private val noteService: NoteService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String) = userService.requireUser(id).toVo3()

    @GetMapping("/{id}/notes")
    fun getUserNotes(@PathVariable id: String, pageable: Pageable) =
        noteService.getUserNotes(id, pageable).map { it.toVo2() }

    @PostMapping("/-/info")
    fun updateInfo(@RequestBody dto: UserInfoDto) = userService.updateInfo(dto).toVo()

    @GetMapping("/-/notes")
    fun getMyNotes(pageable: Pageable) = noteService.getMyNotes(pageable).map { it.toVo2() }

    @GetMapping("/-/trash")
    fun getTrashNotes(pageable: Pageable) = noteService.getTrashNotes(pageable).map { it.toVo2() }

    @DeleteMapping("/-/trash")
    fun cleanTrash() = noteService.cleanTrash()

    @GetMapping("/{id}/notebooks")
    fun getUserNotebooks(@PathVariable id: String, pageable: Pageable) =
        notebookService.getUserNotebooks(id, pageable).map { it.toVo() }

    @GetMapping("/-/notebooks")
    fun getMyNotebooks(q: String?, pageable: Pageable) = notebookService.getMyNotebooks(q, pageable).map { it.toVo() }

}
