package cn.har01d.notebook.service

import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppUnauthorizedException
import cn.har01d.notebook.dto.AccountDto
import cn.har01d.notebook.entity.Notebook
import cn.har01d.notebook.entity.NotebookRepository
import cn.har01d.notebook.entity.User
import cn.har01d.notebook.entity.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
        private val repository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val notebookRepository: NotebookRepository
) {
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

    fun requireUser(id: Int): User {
        return repository.findByIdOrNull(id) ?: throw AppUnauthorizedException("用户不存在")
    }

    fun requireCurrentUser(): User {
        return getCurrentUser() ?: throw AppUnauthorizedException("用户未登录")
    }

    fun createUser(dto: AccountDto): User {
        if (repository.existsByUsername(dto.username)) {
            throw AppForbiddenException("用户名已经存在")
        }
        val user = User(dto.username, passwordEncoder.encode(dto.password))
        repository.save(user)
        val notebook = Notebook("我的笔记本", "我的第一个笔记本", user)
        notebookRepository.save(notebook)
        return user
    }
}