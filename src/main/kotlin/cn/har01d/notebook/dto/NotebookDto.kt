package cn.har01d.notebook.dto

import cn.har01d.notebook.core.Access

data class NotebookDto(val name: String, val description: String, val access: Access? = null)
