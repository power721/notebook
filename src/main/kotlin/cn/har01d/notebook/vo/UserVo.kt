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
        val editorMode: String?,
        val mdTheme: String?,
        val signature: String?,
        val avatar: String?,
        val role: Role,
        val createdTime: Instant
)

data class UserVo2(
        val id: String,
        val username: String,
        val role: Role,
        val avatar: String?,
)

data class UserVo4(
        val id: String,
        val username: String,
        val mdTheme: String?,
        val signature: String?,
        val avatar: String?,
)

data class UserVo3(
        val id: String,
        val username: String,
        val mdTheme: String?,
        val signature: String?,
        val avatar: String?,
        val role: Role,
        val createdTime: Instant
)

fun User.toVo() = UserVo(IdUtils.encode(id!! + USER_OFFSET), username, email, editorMode, mdTheme, signature, avatar, role, createdTime)
fun User.toVo2() = UserVo2(IdUtils.encode(id!! + USER_OFFSET), username, role, avatar)
fun User.toVo4() = UserVo4(IdUtils.encode(id!! + USER_OFFSET), username, mdTheme, signature, avatar)
fun User.toVo3() = UserVo3(IdUtils.encode(id!! + USER_OFFSET), username, mdTheme, signature, avatar, role, createdTime)
