package cn.har01d.notebook.vo

import cn.har01d.notebook.core.Role
import cn.har01d.notebook.entity.User
import cn.har01d.notebook.service.IdUtils
import cn.har01d.notebook.service.IdUtils.USER_OFFSET
import java.time.Instant

data class UserVo(
        val id: String,
        val username: String,
        val email: String?,
        val role: Role,
        val createdTime: Instant
)

data class UserVo2(
        val id: String,
        val username: String
)

fun User.toVo() = UserVo(IdUtils.encode(id!! + USER_OFFSET), username, email, role, createdTime)
fun User.toVo2() = UserVo2(IdUtils.encode(id!! + USER_OFFSET), username)
