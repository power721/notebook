package cn.har01d.notebook.controller

import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.service.AdminService
import cn.har01d.notebook.service.AuditService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RequestMapping("/admin")
@RestController
class AdminController(private val service: AdminService, private val auditService: AuditService) {

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
}
