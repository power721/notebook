package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.NoteContent
import java.time.Instant

data class NoteContentVo(
        val title: String,
        val markdown: Boolean,
        val version: Int,
        val createdTime: Instant
)

fun NoteContent.toVo() = NoteContentVo(title, markdown, version, createdTime)
