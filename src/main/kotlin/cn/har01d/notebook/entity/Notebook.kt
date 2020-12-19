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
        @JoinColumn(foreignKey = ForeignKey(name = "FK_OWNER_ID"))
        @ManyToOne val owner: User,
        var updatedTime: Instant? = null,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NotebookRepository : JpaRepository<Notebook, Int> {
    fun countByOwner(user: User): Long
    fun existsByOwnerAndName(user: User, name: String): Boolean
    fun findByOwnerAndName(user: User, name: String): Notebook?
    fun findByOwner(user: User, pageable: Pageable): Page<Notebook>
    fun findByNameContains(text: String, pageable: Pageable): Page<Notebook>
    fun findByOwnerAndNameContains(user: User, text: String, pageable: Pageable): Page<Notebook>
}
