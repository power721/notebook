package cn.har01d.notebook.core

import cn.har01d.notebook.core.config.QiniuProperties

data class SiteConfig(
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
        val qiniu: QiniuProperties,
)
