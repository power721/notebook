package cn.har01d.notebook.core

data class SiteConfig(
        val siteName: String,
        val brandColor: String,
        val qrCode: String,
        val icpBeian: String,
        val govBeian: String,
        val showViews: Boolean,
        val enableAudit: Boolean,
        val enableComment: Boolean,
        val enableSignup: Boolean
)
