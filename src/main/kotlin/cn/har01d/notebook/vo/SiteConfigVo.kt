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
    val enableFileUpload: Boolean,
    val enableImageUpload: Boolean,
    val enableSignup: Boolean,
    val enableHeartbeat: Boolean,
    val github: String,
)

data class SiteConfigVo2(
    val siteName: String,
    val brandColor: String,
    val qrCode: String,
    val icpBeian: String,
    val govBeian: String,
    val showViews: Boolean,
    val showWords: Boolean,
    val enableAudit: Boolean,
    val enableComment: Boolean,
    val enableFileUpload: Boolean,
    val enableImageUpload: Boolean,
    val enableSignup: Boolean,
    val enableHeartbeat: Boolean,
    val secretKey: String,
    val github: String,
)

fun SiteConfig.toVo() = if (enableEncrypt) SiteConfigVo2(
    siteName,
    brandColor,
    qrCode,
    icpBeian,
    govBeian,
    showViews,
    showWords,
    enableAudit,
    enableComment,
    enableFileUpload,
    enableImageUpload,
    enableSignup,
    enableHeartbeat,
    secretKey,
    github
) else SiteConfigVo(
    siteName,
    brandColor,
    qrCode,
    icpBeian,
    govBeian,
    showViews,
    showWords,
    enableAudit,
    enableComment,
    enableFileUpload,
    enableImageUpload,
    enableSignup,
    enableHeartbeat,
    github,
)
