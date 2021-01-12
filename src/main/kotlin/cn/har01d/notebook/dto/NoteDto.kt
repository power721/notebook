package cn.har01d.notebook.dto

import cn.har01d.notebook.core.Access

data class NoteDto(
        val title: String,
        val content: String,
        val markdown: Boolean,
        val tags: List<TagDto>?,
        val slug: String? = null,
        val notebookId: String? = null,
        val categoryId: String? = null,
        val access: Access? = null
)

data class TagDto(val name: String)
