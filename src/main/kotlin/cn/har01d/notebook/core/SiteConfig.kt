package cn.har01d.notebook.core

data class SiteConfig(
        val siteName: String,
        val icpBeian: String,
        val govBeian: String,
        val enableComment: Boolean,
        val enableSignup: Boolean
)
