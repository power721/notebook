package cn.har01d.notebook.entity

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class Notebook(
        @Column(nullable = false) var name: String,
        @Column(nullable = false) var description: String,
        @ManyToOne val owner: User,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NotebookRepository : JpaRepository<Notebook, Int> {
    fun existsByOwnerAndName(user: User, name: String): Boolean
    fun findByOwnerAndName(user: User, name: String): Notebook?
    fun findByOwner(user: User, pageable: Pageable): Page<Notebook>
    fun findByNameContains(text: String, pageable: Pageable): Page<Notebook>
    fun findByOwnerAndNameContains(user: User, text: String, pageable: Pageable): Page<Notebook>
}