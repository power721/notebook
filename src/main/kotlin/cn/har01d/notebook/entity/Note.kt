package cn.har01d.notebook.entity

import cn.har01d.notebook.core.Access
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import javax.persistence.*

@Entity
class Note(
        @ManyToOne val author: User,
        @ManyToOne var notebook: Notebook,
        @ManyToOne var category: Category,
        @OneToOne var content: NoteContent? = null,
        @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false) var access: Access = Access.PUBLIC,
        @JsonProperty("id") @Column(nullable = false, unique = true) var rid: String,
        @Column(nullable = false) var views: Int = 0,
        var updatedTime: Instant? = null,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NoteRepository : JpaRepository<Note, Int> {
    fun findByRid(rid: String): Note?
    fun findByAccess(access: Access, pageable: Pageable): Page<Note>
    fun findByNotebook(notebook: Notebook, pageable: Pageable): Page<Note>
    fun findByNotebookAndAccess(notebook: Notebook, access: Access, pageable: Pageable): Page<Note>
    fun findByCategoryAndAccess(category: Category, access: Access, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.category=?1 and (n.access=?2 or n.author=?3)")
    fun findByCategoryAndAccessOrAuthor(category: Category, access: Access, user: User, pageable: Pageable): Page<Note>
    fun findByAccessOrAuthor(access: Access, user: User, pageable: Pageable): Page<Note>
    fun findByAuthor(user: User, pageable: Pageable): Page<Note>
    fun existsByRid(rid: String): Boolean
}
