package cn.har01d.notebook.controller

import cn.har01d.notebook.entity.NoteRepository
import cn.har01d.notebook.service.*
import cn.har01d.notebook.vo.toVo
import cn.har01d.notebook.vo.toVo2
import cn.har01d.notebook.vo.toVo3
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Controller
class Html5Controller(
        private val noteRepository: NoteRepository,
        private val noteService: NoteService,
        private val notebookService: NotebookService,
        private val categoryService: CategoryService,
        private val tagService: TagService,
        private val userService: UserService,
        private val configService: ConfigService,
        private val menuService: MenuService,
) {
    @ModelAttribute("siteConfig")
    fun siteConfig() = configService.getSiteConfig()

    @ModelAttribute("menus")
    fun menus() = menuService.getMenus()

    @ModelAttribute("url")
    fun url(request: HttpServletRequest): String = request.requestURI

    @GetMapping("/home.html")
    fun home(model: Model): String {
        return "home"
    }

    @GetMapping("/about.html")
    fun about(model: Model): String {
        return "about"
    }

    @GetMapping("/sitemap.xml", produces = ["text/xml"])
    fun sitemap(request: HttpServletRequest, model: Model): String {
        val scheme = request.scheme
        val host = request.serverName
        val port = request.serverPort
        val base = StringBuilder()
        base.append(if (port == 443) "https" else scheme).append("://").append(host)
        if ((scheme == "http" && port != 80) || (scheme == "https" && port != 443)) {
            base.append(":").append(port)
        }
        base.append(request.contextPath)
        model.addAttribute("base", base.toString())
        val notebooks = notebookService.list("", PageRequest.of(0, 50)).map { it.toVo() }
        model.addAttribute("notebooks", notebooks.content)
        val categories = categoryService.list("", PageRequest.of(0, 50)).map { it.toVo() }
        model.addAttribute("categories", categories.content)
        val tags = tagService.list("", PageRequest.of(0, 50))
        model.addAttribute("tags", tags.content)
        return "sitemap"
    }

    @GetMapping("/notes.html", "/articles.html")
    fun notes(q: String?, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notes = noteService.list(q, pageable).map { it.toVo2() }
        model.addAttribute("q", q ?: "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notes.totalPages)
        model.addAttribute("total", notes.totalElements)
        model.addAttribute("notes", notes.content)
        return "notes"
    }

    @Transactional
    @GetMapping("/notes/{id}.html", "/articles/{id}.html")
    fun note(@PathVariable id: String, model: Model): String {
        val note = noteService.get(id)
        note.views = note.views + 1
        noteRepository.save(note)
        model.addAttribute("note", note.toVo())
        return "note"
    }

    @GetMapping("/notebooks.html")
    fun notebooks(q: String?, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notebooks = notebookService.list(q, pageable).map { it.toVo() }
        model.addAttribute("q", q ?: "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notebooks.totalPages)
        model.addAttribute("total", notebooks.totalElements)
        model.addAttribute("notebooks", notebooks.content)
        return "notebooks"
    }

    @GetMapping("/notebooks/{id}.html")
    fun notebook(@PathVariable id: String, q: String?, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        model.addAttribute("notebook", notebookService.get(id).toVo())
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notes = notebookService.getNotes(id, pageable).map { it.toVo2() }
        model.addAttribute("q", q ?: "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notes.totalPages)
        model.addAttribute("total", notes.totalElements)
        model.addAttribute("notes", notes.content)
        return "notebook"
    }

    @GetMapping("/categories.html")
    fun categories(q: String?, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "id")
        val categories = categoryService.list(q, pageable).map { it.toVo() }
        model.addAttribute("q", q ?: "")
        model.addAttribute("page", page)
        model.addAttribute("pages", categories.totalPages)
        model.addAttribute("total", categories.totalElements)
        model.addAttribute("categories", categories.content)
        return "categories"
    }

    @GetMapping("/categories/{id}.html")
    fun category(@PathVariable id: String, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        model.addAttribute("category", categoryService.get(id))
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notes = categoryService.getNotes(id, pageable).map { it.toVo2() }
        model.addAttribute("q", "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notes.totalPages)
        model.addAttribute("total", notes.totalElements)
        model.addAttribute("notes", notes.content)
        return "category"
    }

    @GetMapping("/tags.html")
    fun tags(q: String?, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        val pageable = PageRequest.of(page - 1, 100)
        val tags = tagService.list(q, pageable)
        model.addAttribute("q", q ?: "")
        model.addAttribute("page", page)
        model.addAttribute("pages", tags.totalPages)
        model.addAttribute("total", tags.totalElements)
        model.addAttribute("tags", tags.content)
        return "tags"
    }

    @GetMapping("/tags/{id}.html")
    fun tag(@PathVariable id: String, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        model.addAttribute("tag", id)
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notes = tagService.getNotes(id, pageable).map { it.toVo2() }
        model.addAttribute("q", "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notes.totalPages)
        model.addAttribute("total", notes.totalElements)
        model.addAttribute("notes", notes.content)
        return "tag"
    }

    @GetMapping("/users/{id}.html")
    fun user(@PathVariable id: String, @RequestParam(defaultValue = "1") page: Int, model: Model): String {
        model.addAttribute("user", userService.requireUser(id).toVo3())
        val pageable = PageRequest.of(page - 1, 20, Sort.Direction.DESC, "updatedTime")
        val notes = noteService.getUserNotes(id, pageable).map { it.toVo2() }
        model.addAttribute("q", "")
        model.addAttribute("page", page)
        model.addAttribute("pages", notes.totalPages)
        model.addAttribute("total", notes.totalElements)
        model.addAttribute("notes", notes.content)
        return "user"
    }
}
