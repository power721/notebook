package cn.har01d.notebook.entity

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
data class Captcha(
    @Column(nullable = false) val name: String,
    @Column(nullable = false) val code: String,
    @Column(nullable = false) val expireTime: Instant,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface CaptchaRepository : JpaRepository<Captcha, Int> {
    fun findByName(name: String): Captcha?
    fun deleteAllByName(name: String)
}
