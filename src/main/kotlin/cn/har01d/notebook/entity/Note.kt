package cn.har01d.notebook.entity

import cn.har01d.notebook.core.Access
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import javax.persistence.*

@Entity
class Note(
        @JoinColumn(foreignKey = ForeignKey(name = "FK_AUTHOR_ID"))
        @ManyToOne val author: User,
        @JoinColumn(foreignKey = ForeignKey(name = "FK_NOTEBOOK_ID"))
        @ManyToOne var notebook: Notebook,
        @JoinColumn(foreignKey = ForeignKey(name = "FK_CATEGORY_ID"))
        @ManyToOne var category: Category,
        @JoinTable(joinColumns = [JoinColumn(name = "note_id")], inverseJoinColumns = [JoinColumn(name = "tag_id")], foreignKey = ForeignKey(name = "FK_NOTE_ID"), inverseForeignKey = ForeignKey(name = "FK_TAG_ID"))
        @ManyToMany var tags: List<Tag> = ArrayList(),
        @JoinColumn(foreignKey = ForeignKey(name = "FK_CONTENT_ID"))
        @OneToOne var content: NoteContent? = null,
        @Column(unique = true) var slug: String? = null,
        @Enumerated(EnumType.STRING) @Column(length = 16, nullable = false) var access: Access = Access.PUBLIC,
        @JsonProperty("id") @Column(nullable = false, unique = true) var rid: String,
        @Column(nullable = false) var views: Int = 0,
        @Column(nullable = false) var deleted: Boolean = false,
        @Column(nullable = false) var updatedTime: Instant = Instant.now(),
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Transient var words: Int = 0,
        @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NoteRepository : JpaRepository<Note, Int> {
    fun countByNotebook(notebook: Notebook): Long
    fun countByDeleted(deleted: Boolean): Long
    fun countByTagsContains(tag: Tag): Long

    @Query("SELECT count(n) from Note n where n.access=?1 and n.deleted=false")
    fun countByAccess(access: Access): Long

    @Query("SELECT sum(n.views) from Note n")
    fun views(): Long

    fun deleteAllByNotebook(notebook: Notebook)
    fun findByRid(rid: String): Note?
    fun existsByRid(rid: String): Boolean
    fun findBySlug(slug: String): Note?
    fun existsBySlug(slug: String): Boolean

    @Query("SELECT n from Note n where n.access='PUBLIC' and n.deleted=false")
    fun findPublic(pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where (n.access='PUBLIC' or n.author=?1) and n.deleted=false")
    fun findPublicOrOwn(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.content.title like %?1% and n.access='PUBLIC' and n.deleted=false")
    fun searchPublic(text: String, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.content.title like %?1% and (n.access='PUBLIC' or n.author=?2) and n.deleted=false")
    fun searchPublicOrOwn(text: String, user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.access='PUBLIC' and n.author=?1 and n.deleted=false")
    fun findPublicAndAuthor(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.author=?1 and n.deleted=false")
    fun findByAuthor(user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.author=?1 and n.deleted=true")
    fun findByAuthorAndDeleted(user: User, pageable: Pageable): Page<Note>

    fun deleteAllByAuthorAndDeletedTrue(user: User)

    @Query("SELECT n from Note n where n.notebook=?1 and n.deleted=false")
    fun findByNotebook(notebook: Notebook, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.notebook=?1 and n.access=?2 and n.deleted=false")
    fun findByNotebookAndAccess(notebook: Notebook, access: Access, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.category=?1 and n.access='PUBLIC' and n.deleted=false")
    fun findByCategoryAndPublic(category: Category, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where n.category=?1 and (n.access='PUBLIC' or n.author=?2) and n.deleted=false")
    fun findByCategoryAndPublicOrOwn(category: Category, user: User, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where ?1 MEMBER OF n.tags and n.access='PUBLIC' and n.deleted=false")
    fun findByTagAndPublic(tag: Tag, pageable: Pageable): Page<Note>

    @Query("SELECT n from Note n where ?1 MEMBER OF n.tags and (n.access='PUBLIC' or n.author=?2) and n.deleted=false")
    fun findByTagAndPublicOrOwn(tag: Tag, user: User, pageable: Pageable): Page<Note>

    @Modifying
    @Query("UPDATE Note n set n.access='SECRET' where n.access='PUBLIC' and n.notebook=?1")
    fun updateSecretNotebook(notebook: Notebook)

    @Modifying
    @Query("UPDATE Note n set n.access='PRIVATE' where n.access<>'PRIVATE' and n.notebook=?1")
    fun updatePrivateNotebook(notebook: Notebook)

    @Modifying
    @Query("UPDATE Note n SET n.access=?2 WHERE n.notebook=?1")
    fun updateNoteAccess(notebook: Notebook, access: Access)
}
