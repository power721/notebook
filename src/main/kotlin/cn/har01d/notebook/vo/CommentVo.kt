package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.Comment
import java.time.Instant

data class CommentVo(
    val user: UserVo2,
    val content: String,
    val createdTime: Instant,
    val id: Int
)

fun Comment.toVo() = CommentVo(user.toVo2(), content, createdTime, id!!)
