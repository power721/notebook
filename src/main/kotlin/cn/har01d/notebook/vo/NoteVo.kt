package cn.har01d.notebook.vo

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.entity.Note
import java.time.Instant

data class NoteVO(
        val id: String,
        val title: String,
        val content: String,
        val author: UserVo2,
        val notebook: NotebookVo2,
        val category: CategoryVo2,
        val markdown: Boolean,
        val access: Access,
        val version: Int,
        val views: Int,
        val createdTime: Instant,
        val updatedTime: Instant?
)

data class NoteVO2(
        val id: String,
        val title: String,
        val author: UserVo2,
        val notebook: NotebookVo2,
        val category: CategoryVo2,
        val version: Int,
        val createdTime: Instant,
        val updatedTime: Instant?
)

fun Note.toVo() = NoteVO(rid, content!!.title, content!!.content, author.toVo2(), notebook.toVo2(), category.toVo2(), content!!.markdown, access, content!!.version, views, createdTime, updatedTime)
fun Note.toVo2() = NoteVO2(rid, content!!.title, author.toVo2(), notebook.toVo2(), category.toVo2(), content!!.version, createdTime, updatedTime)

fun String.truncate(limit: Int = 100) = if (length > limit) substring(0, limit) + "..." else this
