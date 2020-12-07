package cn.har01d.notebook.entity

import cn.har01d.notebook.core.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class User(
        @Column(length = 32, unique = true, nullable = false) var username: String,
        @JsonIgnore @Column(nullable = false) var password: String,
        @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false) var role: Role = Role.ROLE_USER,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
