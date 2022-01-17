package cn.har01d.notebook.vo

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.entity.Notebook
import cn.har01d.notebook.service.IdUtils
import cn.har01d.notebook.service.IdUtils.NOTEBOOK_OFFSET
import java.time.Instant

data class NotebookVo(
        val id: String,
        val name: String,
        val slug: String?,
        val description: String,
        val owner: UserVo2,
        val access: Access,
        val createdTime: Instant,
        val updatedTime: Instant?
)

data class NotebookVo2(
        val id: String,
        val name: String,
        val slug: String?,
        val access: Access,
)

fun Notebook.toVo() = NotebookVo(IdUtils.encode(id!! + NOTEBOOK_OFFSET), name, slug, description, owner.toVo2(), access, createdTime, updatedTime)
fun Notebook.toVo2() = NotebookVo2(IdUtils.encode(id!! + NOTEBOOK_OFFSET), name, slug, access)
