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
        @Column(nullable = false) var deleted: Boolean = false,
        var updatedTime: Instant = Instant.now(),
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NoteRepository : JpaRepository<Note, Int> {
    fun countByNotebook(notebook: Notebook): Long
    fun deleteAllByNotebook(notebook: Notebook)
    fun findByRid(rid: String): Note?
    fun existsByRid(rid: String): Boolean

    @Query("SELECT n from Note n where n.access='PUBLIC' and n.deleted=false")
    fun findPublic(pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where (n.access='PUBLIC' or n.author=?1) and n.deleted=false")
    fun findPublicOrOwn(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.access='PUBLIC' and n.author=?1 and n.deleted=false")
    fun findPublicAndAuthor(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.author=?1 and n.deleted=false")
    fun findByAuthor(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.author=?1 and n.deleted=true")
    fun findByAuthorAndDeleted(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.notebook=?1 and n.deleted=false")
    fun findByNotebook(notebook: Notebook, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.notebook=?1 and n.access='PUBLIC' and n.deleted=false")
    fun findByNotebookAndPublic(notebook: Notebook, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.category=?1 and n.access='PUBLIC' and n.deleted=false")
    fun findByCategoryAndPublic(category: Category, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.category=?1 and (n.access='PUBLIC' or n.author=?2) and n.deleted=false")
    fun findByCategoryAndPublicOrOwn(category: Category, user: User, pageable: Pageable): Page<Note>
}
