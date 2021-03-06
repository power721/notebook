package cn.har01d.notebook.vo

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.dto.TagDto
import cn.har01d.notebook.entity.Note
import java.time.Instant

data class NoteVO(
        var id: String,
        val title: String,
        val slug: String?,
        val content: String,
        val author: UserVo4,
        val notebook: NotebookVo2,
        val category: CategoryVo2,
        val tags: List<TagDto>,
        val markdown: Boolean,
        val access: Access,
        val version: Int,
        val views: Int,
        val deleted: Boolean,
        val createdTime: Instant,
        val updatedTime: Instant?,
        val words: Int
)

data class NoteVO2(
        val id: String,
        val title: String,
        val slug: String?,
        val author: UserVo2,
        val notebook: NotebookVo2,
        val category: CategoryVo2,
        val access: Access,
        val version: Int,
        val createdTime: Instant,
        val updatedTime: Instant?
)

fun Note.toVo() = NoteVO(rid, content!!.title, slug, content!!.content, author.toVo4(), notebook.toVo2(), category.toVo2(), tags.map { TagDto(it.name) }, content!!.markdown, access, content!!.version, views, deleted, createdTime, updatedTime, words)
fun Note.toVo2() = NoteVO2(if (slug!=null) "" else rid, content!!.title, slug, author.toVo2(), notebook.toVo2(), category.toVo2(), access, content!!.version, createdTime, updatedTime)

fun String.truncate(limit: Int = 100) = if (length > limit) substring(0, limit) + "..." else this
