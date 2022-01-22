package cn.har01d.notebook.controller

import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.entity.Config
import cn.har01d.notebook.entity.Menu
import cn.har01d.notebook.service.ConfigService
import cn.har01d.notebook.service.MenuService
import cn.har01d.notebook.vo.toVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config")
class ConfigController(private val configService: ConfigService, private val menuService: MenuService) {
    @GetMapping
    fun findAll(): List<Config> = configService.findAll()

    @GetMapping("/site")
    fun getSiteConfig() = configService.getSiteConfig().toVo()

    @PutMapping("/site")
    fun updateSiteConfig(@RequestBody siteConfig: SiteConfig) = configService.updateSiteConfig(siteConfig)

    @GetMapping("/menus")
    fun getMenus(): List<Menu> {
        return menuService.getMenus()
    }

    @GetMapping("/{name}")
    fun get(@PathVariable name: String) = configService.get(name)

    @PostMapping
    fun save(@RequestBody config: Config) = configService.save(config)
}
