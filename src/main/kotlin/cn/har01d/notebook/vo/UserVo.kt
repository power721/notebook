package cn.har01d.notebook.vo

import cn.har01d.notebook.core.Role
import cn.har01d.notebook.entity.User
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.IdUtils.USER_OFFSET
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

data class UserVo3(
        val id: String,
        val username: String,
        val role: Role,
        val createdTime: Instant
)

fun User.toVo() = UserVo(IdUtils.encode(id!! + USER_OFFSET), username, email, role, createdTime)
fun User.toVo2() = UserVo2(IdUtils.encode(id!! + USER_OFFSET), username)
fun User.toVo3() = UserVo3(IdUtils.encode(id!! + USER_OFFSET), username, role, createdTime)
