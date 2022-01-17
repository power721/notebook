package cn.har01d.notebook.entity

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class Category(
        @Column(nullable = false, unique = true) var name: String,
        @Column(nullable = false) var description: String = "",
        @Column(unique = true) var slug: String? = null,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface CategoryRepository : JpaRepository<Category, Int> {
    fun existsBySlug(slug: String): Boolean
    fun findBySlug(slug: String): Category?
    fun existsByName(name: String): Boolean
    fun findByName(name: String): Category?
    fun findByNameContains(text: String, pageable: Pageable): Page<Category>
}
