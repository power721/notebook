package cn.har01d.notebook.entity

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
data class Comment(
    @JoinColumn(foreignKey = ForeignKey(name = "FK_COMMENT_USER_ID"))
    @ManyToOne val user: User,
    @JoinColumn(foreignKey = ForeignKey(name = "FK_COMMENT_NOTE_ID"))
    @ManyToOne val note: Note,
    @Column(columnDefinition = "TEXT NOT NULL") val content: String,
    @Column(nullable = false) val createdTime: Instant = Instant.now(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface CommentRepository : JpaRepository<Comment, Int> {
    fun findByNote(note: Note, pageable: Pageable): Page<Comment>
}
