package cn.har01d.notebook.core.config

data class QiniuProperties(
    val enabled: Boolean = false,
    val accessKey: String = "",
    val secretKey: String = "",
    val bucket: String = "",
    val domain: String = "",
)
