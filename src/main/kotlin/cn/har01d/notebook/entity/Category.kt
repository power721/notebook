package cn.har01d.notebook.entity

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class Category(
        @Column(nullable = false, unique = true) var name: String,
        @Column(nullable = false) var description: String = "",
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface CategoryRepository : JpaRepository<Category, Int> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Category?
}
