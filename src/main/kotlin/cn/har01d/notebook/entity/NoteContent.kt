package cn.har01d.notebook.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class NoteContent(
        @Column(nullable = false) var title: String,
        @Column(columnDefinition = "TEXT", nullable = false) var content: String,
        @JsonIgnore @OneToOne val note: Note,
        @Column(nullable = false) var markdown: Boolean = false,
        @Column(nullable = false) var version: Int = 1,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @JsonIgnore @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface NoteContentRepository : JpaRepository<NoteContent, Int> {
    fun deleteAllByNote(note: Note)
    fun findByNoteOrderByIdDesc(note: Note): List<NoteContent>
    fun findByNoteAndVersion(note: Note, version: Int): NoteContent?
}