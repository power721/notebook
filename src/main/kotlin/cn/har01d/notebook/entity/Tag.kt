package cn.har01d.notebook.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
class Tag(
        @Column(length = 32, unique = true, nullable = false) val name: String,
        @JsonIgnore @ManyToMany(mappedBy = "tags") var notes: List<Note> = ArrayList(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface TagRepository : JpaRepository<Tag, Int> {
    fun findByName(name: String): Tag?
    fun findByNameContains(text: String, pageable: Pageable): Page<Tag>
}
