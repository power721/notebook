package cn.har01d.notebook.controller

import cn.har01d.notebook.service.CaptchaService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/captcha")
class CaptchaController(private val service: CaptchaService) {
    @GetMapping
    fun generate(name: String, response: HttpServletResponse) = service.generate(name, response)
}
