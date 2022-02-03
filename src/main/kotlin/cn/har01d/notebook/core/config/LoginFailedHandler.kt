package cn.har01d.notebook.core.config

import cn.har01d.notebook.core.Error
import cn.har01d.notebook.core.exception.AppUnauthorizedException
import cn.har01d.notebook.entity.UserRepository
import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.CacheService
import cn.har01d.notebook.service.CaptchaService
import cn.har01d.notebook.util.getClientIp
import cn.spark2fire.auth.dto.LoginDto
import cn.spark2fire.auth.handler.UserAuthHandler
import com.github.benmanes.caffeine.cache.Cache
import org.springframework.stereotype.Component
import java.time.Duration

private const val CAPTCHA_THRESHOLD = 5
private const val RETRY_MAX = 10

@Component
class LoginFailedHandler(
    private val userRepository: UserRepository,
    private val captchaService: CaptchaService,
    private val auditService: AuditService,
    private val cacheService: CacheService,
) : UserAuthHandler() {
    private val cache: Cache<String, Int> = cacheService.intCache("Login", Duration.ofMinutes(5))

    override fun preLogin(account: LoginDto) {
        val ip = getClientIp()
        val user = userRepository.findByUsername(account.username)
        val key = if (user == null) "$ip" else "${user.id}-$ip"
        val count = cache.getIfPresent(key) ?: 0

        if (count > RETRY_MAX) {
            throw AppUnauthorizedException("请5分钟后再尝试", Error.LOGIN_FAILED_MAX)
        } else if (count > CAPTCHA_THRESHOLD && !captchaService.validate(account.username, account.captcha)) {
            cache.put(key, count + 1)
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
        if (count >= CAPTCHA_THRESHOLD) {
            throw AppUnauthorizedException("用户或密码错误", Error.CAPTCHA_THRESHOLD)
        }
    }

    override fun onLogoutSuccess(username: String) {
        val user = userRepository.findByUsername(username)
        user?.let {
            auditService.auditLogout(it)
        }
    }
}
