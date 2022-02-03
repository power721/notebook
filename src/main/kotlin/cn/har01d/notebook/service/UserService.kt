package cn.har01d.notebook.service

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.Role
import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppUnauthorizedException
import cn.har01d.notebook.dto.AccountDto
import cn.har01d.notebook.dto.UserDto
import cn.har01d.notebook.dto.UserInfoDto
import cn.har01d.notebook.entity.Notebook
import cn.har01d.notebook.entity.NotebookRepository
import cn.har01d.notebook.entity.User
import cn.har01d.notebook.entity.UserRepository
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.IdUtils.USER_OFFSET
import cn.har01d.notebook.util.getClientIp
import cn.har01d.notebook.vo.UserStats
import com.github.benmanes.caffeine.cache.Cache
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class UserService(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val notebookRepository: NotebookRepository,
    private val configService: ConfigService,
    private val auditService: AuditService,
    private val cacheService: CacheService,
) {
    private val cache: Cache<String, Boolean> = cacheService.boolCache("User", Duration.ofMinutes(1))

    fun getCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            return repository.findByUsername(authentication.name)
        }

        return null
    }

    fun getUser(id: Int): User? {
        return repository.findByIdOrNull(id)
    }

    fun requireUser(id: String): User {
        return repository.findByIdOrNull(IdUtils.decode(id) - USER_OFFSET) ?: throw AppUnauthorizedException("用户不存在")
    }

    fun requireCurrentUser(): User {
        return getCurrentUser() ?: throw AppUnauthorizedException("用户未登录")
    }

    fun signup(dto: AccountDto): User {
        val enabled = configService.get(Const.ENABLE_SIGNUP, true)
        if (!enabled) {
            throw AppForbiddenException("禁止用户注册")
        }
        return createUser(dto).also { auditService.auditSignUp(it) }
    }

    fun createUser(dto: AccountDto): User {
        if (repository.existsByUsername(dto.username)) {
            throw AppForbiddenException("用户名已经存在")
        }
        val user = User(dto.username, passwordEncoder.encode(dto.password))
        if (repository.count() == 0L) {
            user.role = Role.ROLE_ADMIN
        }
        repository.save(user)
        val notebook = Notebook("${user.username}的笔记本", "${user.username}的第一个笔记本", user)
        notebookRepository.save(notebook)
        return user
    }

    fun update(dto: UserDto): User {
        val user = requireCurrentUser()
        if (dto.email.isNotEmpty()) {
            val exist = repository.findByEmail(dto.email)
            if (exist != null && exist.id != user.id) {
                throw AppException("邮箱已被注册")
            }
            user.email = dto.email
        }
        if (dto.newPassword.isNotEmpty()) {
            if (!passwordEncoder.matches(dto.password, user.password)) {
                throw AppException("原密码不正确")
            }
            user.password = passwordEncoder.encode(dto.newPassword)
        }
        return repository.save(user).also { auditService.auditUserUpdate(it) }
    }

    fun updateInfo(dto: UserInfoDto): User {
        val user = requireCurrentUser()
        user.editorMode = dto.editorMode
        user.mdTheme = dto.mdTheme
        user.signature = dto.signature
        return repository.save(user).also { auditService.auditUserUpdate(it) }
    }

    fun heartbeat(): SiteConfig {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.name != "anonymousUser") {
            cache.put(authentication.name, true)
        } else {
            val ip = getClientIp()
            if (ip != null) {
                cache.put(ip, false)
            }
        }
        return configService.getSiteConfig()
    }

    fun stats() = UserStats(repository.count(), cache.estimatedSize(), cache.asMap().filterValues { it == false }.size)
}
