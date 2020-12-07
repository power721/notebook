package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.Notebook
import cn.har01d.notebook.service.IdUtils
import cn.har01d.notebook.service.IdUtils.NOTEBOOK_OFFSET
import java.time.Instant

data class NotebookVo(
        val id: String,
        val name: String,
        val description: String,
        val owner: UserVo2,
        val createdTime: Instant
)

data class NotebookVo2(
        val id: String,
        val name: String
)

fun Notebook.toVo() = NotebookVo(IdUtils.encode(id!! + NOTEBOOK_OFFSET), name, description, owner.toVo2(), createdTime)
fun Notebook.toVo2() = NotebookVo2(IdUtils.encode(id!! + NOTEBOOK_OFFSET), name)
