package cn.har01d.notebook.entity

import cn.har01d.notebook.core.ActionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import javax.persistence.*

@Entity
class Audit(
        @JoinColumn(foreignKey = ForeignKey(name = "FK_AUDIT_OPERATOR_USER_ID"))
        @ManyToOne val operator: User,
        @Enumerated(EnumType.STRING) @Column(nullable = false) val type: ActionType,
        @Column(columnDefinition = "TEXT NOT NULL") val content: String,
        @Column(nullable = false) val target: Int,
        @Column(length = 1024) val userAgent: String? = null,
        @Column(length = 50) val clientIp: String? = null,
        @Column(length = 1024) val referer: String? = null,
        @Column(nullable = false) val createdTime: Instant = Instant.now(),
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null
)

interface AuditRepository : JpaRepository<Audit, Int> {
    fun findByOperatorAndType(user: User, type: ActionType, pageable: Pageable): Page<Audit>
}
