package cn.har01d.notebook.controller

import cn.har01d.notebook.dto.AccountDto
import cn.har01d.notebook.service.UserService
import cn.har01d.notebook.vo.toVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/accounts")
class AccountController(val userService: UserService) {
    @PostMapping("/signup")
    fun signup(@RequestBody account: AccountDto) = userService.createUser(account).toVo()

    @GetMapping("/info")
    fun info() = userService.requireCurrentUser().toVo()
}