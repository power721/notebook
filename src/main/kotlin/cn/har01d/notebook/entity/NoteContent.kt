package cn.har01d.notebook.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import javax.persistence.*

@Entity
class NoteContent(
        @Column(nullable = false) var title: String,
        @Column(columnDefinition = "LONGTEXT NOT NULL") var content: String,
        @JoinColumn(foreignKey = ForeignKey(name = "FK_CONTENT_NOTE_ID"))
        @JsonIgnore @OneToOne val note: Note,
        @Column(nullable = false) var markdown: Boolean = false,
        @Column(nullable = false) var version: Int = 1,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NoteContentRepository : JpaRepository<NoteContent, Int> {
    fun deleteAllByNote(note: Note)
    fun deleteAllByNoteAuthorAndNoteDeletedTrue(user: User)
    fun findByNoteOrderByIdDesc(note: Note): List<NoteContent>
    fun findByNoteAndVersion(note: Note, version: Int): NoteContent?
    fun deleteByNoteAndVersion(note: Note, version: Int)

    @Query("select max(c.version) from NoteContent c where c.note=?1")
    fun version(note: Note): Int
}
