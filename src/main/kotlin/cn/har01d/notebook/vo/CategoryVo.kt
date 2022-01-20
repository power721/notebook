package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.IdUtils.CATEGORY_OFFSET
import java.time.Instant

data class CategoryVo(
        val id: String,
        val name: String,
        val slug: String?,
        val description: String,
        val createdTime: Instant
)

data class CategoryVo2(
        val id: String,
        val name: String,
        val slug: String?,
)

fun Category.toVo() = CategoryVo(IdUtils.encode(id!! + CATEGORY_OFFSET), name, slug, description, createdTime)
fun Category.toVo2() = CategoryVo2(IdUtils.encode(id!! + CATEGORY_OFFSET), name, slug)
