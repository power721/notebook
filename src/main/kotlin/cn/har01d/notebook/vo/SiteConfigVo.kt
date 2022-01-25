package cn.har01d.notebook.vo

import cn.har01d.notebook.core.SiteConfig

data class SiteConfigVo(
    val siteName: String,
    val brandColor: String,
    val qrCode: String,
    val icpBeian: String,
    val govBeian: String,
    val showViews: Boolean,
    val showWords: Boolean,
    val enableAudit: Boolean,
    val enableComment: Boolean,
    val enableUpload: Boolean,
    val enableSignup: Boolean,
)

fun SiteConfig.toVo() = SiteConfigVo(siteName, brandColor, qrCode, icpBeian, govBeian, showViews, showWords, enableAudit, enableComment, enableFileUpload, enableSignup)
