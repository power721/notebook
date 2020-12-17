package cn.har01d.notebook.controller

import cn.har01d.notebook.service.AdminService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/admin")
@RestController
class AdminController(private val service: AdminService) {
    @GetMapping("/info")
    fun getSystemInfo() = service.getSystemInfo()
}
