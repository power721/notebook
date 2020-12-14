package cn.har01d.notebook.controller

import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.entity.Config
import cn.har01d.notebook.service.ConfigService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config")
class ConfigController(private val service: ConfigService) {
    @GetMapping
    fun findAll(): List<Config> = service.findAll()

    @GetMapping("/site")
    fun getSiteConfig() = service.getSiteConfig()

    @PutMapping("/site")
    fun updateSiteConfig(@RequestBody siteConfig: SiteConfig) = service.updateSiteConfig(siteConfig)

    @GetMapping("/{name}")
    fun get(@PathVariable name: String) = service.get(name)

    @PostMapping
    fun save(@RequestBody config: Config) = service.save(config)
}
