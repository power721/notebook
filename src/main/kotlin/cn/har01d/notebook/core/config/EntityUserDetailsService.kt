package cn.har01d.notebook.core.config

import cn.har01d.notebook.entity.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class EntityUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User $username not found.")
        val authority: GrantedAuthority = SimpleGrantedAuthority(user.role.name)
        val grantedAuthorities: Collection<GrantedAuthority> = listOf(authority)
        return User(user.username, user.password, true, true, true, true, grantedAuthorities)
    }
}
