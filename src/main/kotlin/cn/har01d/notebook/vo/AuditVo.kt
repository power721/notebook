package cn.har01d.notebook.vo

import cn.har01d.notebook.core.ActionType
import cn.har01d.notebook.entity.Audit
import java.time.Instant

data class AuditVo(
        val id: Int,
        val operator: UserVo2,
        val type: ActionType,
        val content: String,
        val target: Int,
        val userAgent: String?,
        val clientIp: String?,
        val referer: String?,
        val createdTime: Instant,
)

fun Audit.toVO() = AuditVo(id!!, operator.toVo2(), type, content, target, userAgent, clientIp, referer, createdTime)
