package cn.har01d.notebook.core.config

import cn.har01d.notebook.core.Error
import cn.har01d.notebook.core.exception.AppUnauthorizedException
import cn.har01d.notebook.entity.UserRepository
import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.CaptchaService
import cn.har01d.notebook.util.getClientIp
import cn.spark2fire.auth.dto.LoginDto
import cn.spark2fire.auth.handler.UserAuthHandler
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

private const val RETRY_MAX = 5

@Component
class LoginFailedHandler(
    private val userRepository: UserRepository,
    private val captchaService: CaptchaService,
    private val auditService: AuditService,
) : UserAuthHandler() {
    private val cache: Cache<String, Int> =
        Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.MINUTES).build()

    override fun preLogin(account: LoginDto) {
        val ip = getClientIp()
        val user = userRepository.findByUsername(account.username)
        val key = if (user == null) "$ip" else "${user.id}-$ip"
        val count = cache.getIfPresent(key) ?: 0

        if (count > RETRY_MAX && !captchaService.validate(account.username, account.captcha)) {
            throw AppUnauthorizedException("验证码错误", Error.CAPTCHA_ERROR)
        }
    }

    override fun onLoginSuccess(username: String) {
        val ip = getClientIp()
        val user = userRepository.findByUsername(username)
        val key = if (user == null) "$ip" else "${user.id}-$ip"
        cache.invalidate(key)
        user?.let {
            auditService.auditLogin(it)
        }
    }

    override fun onLoginFail(username: String) {
        val ip = getClientIp()
        val user = userRepository.findByUsername(username)
        val key = if (user == null) "$ip" else "${user.id}-$ip"

        val count = cache.getIfPresent(key) ?: 0
        cache.put(key, count + 1)
        if (count >= RETRY_MAX) {
            throw AppUnauthorizedException("用户或密码错误", Error.LOGIN_FAILED_MAX)
        }
    }

    override fun onLogoutSuccess(username: String) {
        val user = userRepository.findByUsername(username)
        user?.let {
            auditService.auditLogout(it)
        }
    }
}
