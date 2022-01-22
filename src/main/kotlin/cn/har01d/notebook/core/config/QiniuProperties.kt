package cn.har01d.notebook.core.config

data class QiniuProperties(
    val enabled: Boolean,
    val accessKey: String,
    val secretKey: String,
    val bucket: String,
    val domain: String,
)
