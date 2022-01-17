package cn.har01d.notebook.entity

import cn.har01d.notebook.core.Access
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import javax.persistence.*

@Entity
class Notebook(
        @Column(nullable = false) var name: String,
        @Column(nullable = false) var description: String,
        @JoinColumn(foreignKey = ForeignKey(name = "FK_OWNER_ID"))
        @ManyToOne val owner: User,
        @Column(unique = true) var slug: String? = null,
        @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false) var access: Access = Access.PUBLIC,
        var updatedTime: Instant? = null,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NotebookRepository : JpaRepository<Notebook, Int> {
    fun existsBySlug(slug: String): Boolean
    fun findBySlug(slug: String): Notebook?
    fun countByOwner(user: User): Long
    fun existsByOwnerAndName(user: User, name: String): Boolean
    fun findByOwnerAndName(user: User, name: String): Notebook?
    fun findByAccess(access: Access, pageable: Pageable): Page<Notebook>

    @Query("SELECT n from Notebook n where (n.access='PUBLIC' or n.owner=?1)")
    fun findPublicOrOwner(user: User, pageable: Pageable): Page<Notebook>

    @Query("SELECT n from Notebook n where n.name like %?1% and (n.access='PUBLIC' or n.owner=?2)")
    fun searchPublicOrOwner(text: String, user: User, pageable: Pageable): Page<Notebook>

    fun findByOwner(user: User, pageable: Pageable): Page<Notebook>
    fun findByOwnerAndAccess(user: User, access: Access, pageable: Pageable): Page<Notebook>
    fun findByNameContainsAndAccess(text: String, access: Access, pageable: Pageable): Page<Notebook>
    fun findByOwnerAndNameContains(user: User, text: String, pageable: Pageable): Page<Notebook>
}
