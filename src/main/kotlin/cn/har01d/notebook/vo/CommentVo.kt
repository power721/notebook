package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.Comment
import java.time.Instant

data class CommentVo(
    val id: Int,
    val user: UserVo2,
    val content: String,
    val createdTime: Instant,
)

data class CommentVo2(
    val id: Int,
    val user: UserVo2,
    val content: String,
    val sticky: Boolean,
    val createdTime: Instant,
)

fun Comment.toVo() = CommentVo(id!!, user.toVo2(), content, createdTime)
fun Comment.toVo2() = CommentVo2(id!!, user.toVo2(), content, sticky, createdTime)
