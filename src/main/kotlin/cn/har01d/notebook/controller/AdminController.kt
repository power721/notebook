package cn.har01d.notebook.controller

import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.entity.Menu
import cn.har01d.notebook.service.AdminService
import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.MenuService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RequestMapping("/admin")
@RestController
class AdminController(
        private val service: AdminService,
        private val auditService: AuditService,
        private val menuService: MenuService
) {
    @GetMapping("/config")
    fun getSiteConfig() = service.getSiteConfig()

    @PutMapping("/config")
    fun updateSiteConfig(@RequestBody siteConfig: SiteConfig) = service.updateSiteConfig(siteConfig)

    @GetMapping("/info")
    fun getSystemInfo() = service.getSystemInfo()

    @GetMapping("/stats")
    fun getSystemStats() = service.getSystemStats()

    @GetMapping("/audit")
    fun getAuditList(pageable: Pageable) = auditService.list(pageable)

    @GetMapping("/menus")
    fun getAllMenus(): Page<Menu> {
        return menuService.getMenuList()
    }

    @PostMapping("/menus")
    fun addMenu(@RequestBody menu: Menu): Menu {
        return menuService.addMenu(menu)
    }

    @PostMapping("/menus/{id}")
    fun updateMenu(@PathVariable id: Int, @RequestBody menu: Menu): Menu {
        return menuService.updateMenu(id, menu)
    }

    @DeleteMapping("/menus/{id}")
    fun deleteMenu(@PathVariable id: Int) = menuService.deleteMenu(id)
}
