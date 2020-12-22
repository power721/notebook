package cn.har01d.notebook.service

import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.entity.Menu
import cn.har01d.notebook.entity.MenuRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class MenuService(
        private val repository: MenuRepository,
        private val userService: UserService,
        private val auditService: AuditService,
) {
    fun getMenus(): List<Menu> {
        val sort = Sort.by("order")
        val menus: List<Menu> = repository.findAll(sort)
        val map: MutableMap<Int, Menu> = HashMap()
        for (menu in menus) {
            map[menu.id!!] = menu
        }
        for (menu in menus) {
            val id: Int = menu.parent
            if (id > 0) {
                val parent: Menu? = map[id]
                if (parent != null) {
                    if (parent.children == null) {
                        parent.children = ArrayList()
                    }
                    parent.children?.add(menu)
                }
            }
        }
        return menus.stream().filter { e -> e.parent == 0 }.collect(Collectors.toList())
    }

    fun getMenuList(): Page<Menu> {
        val pageable: Pageable = PageRequest.of(0, 100)
        return repository.findAll(pageable)
    }

    fun addMenu(menu: Menu): Menu {
        return repository.save(menu).also { auditService.auditMenuCreate(userService.requireCurrentUser(), it) }
    }

    fun updateMenu(id: Int, dto: Menu): Menu {
        val menu = repository.findByIdOrNull(id) ?: throw AppNotFoundException("菜单不存在")
        menu.title = dto.title
        menu.uri = dto.uri
        menu.order = dto.order
        menu.parent = dto.parent
        menu.icon = dto.icon
        menu.auth = dto.auth
        menu.admin = dto.admin
        return repository.save(menu).also { auditService.auditMenuUpdate(userService.requireCurrentUser(), it) }
    }

    fun deleteMenu(id: Int) {
        val menu = repository.findByIdOrNull(id) ?: return
        repository.delete(menu)
        auditService.auditMenuDelete(userService.requireCurrentUser(), menu)
    }
}
