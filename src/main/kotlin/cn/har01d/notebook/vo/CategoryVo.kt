package cn.har01d.notebook.vo

import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.service.IdUtils
import cn.har01d.notebook.service.IdUtils.CATEGORY_OFFSET
import java.time.Instant

data class CategoryVo(
        val id: String,
        val name: String,
        val description: String,
        val createdTime: Instant
)

data class CategoryVo2(
        val id: String,
        val name: String
)

fun Category.toVo() = CategoryVo(IdUtils.encode(id!! + CATEGORY_OFFSET), name, description, createdTime)
fun Category.toVo2() = CategoryVo2(IdUtils.encode(id!! + CATEGORY_OFFSET), name)
