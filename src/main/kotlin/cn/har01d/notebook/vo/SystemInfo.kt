package cn.har01d.notebook.vo

data class SystemInfo(
        val ip: String?,
        val hostName: String?,
        val jvmTotalMemory: Long,
        val jvmFreeMemory: Long,
        val jvmCpus: Int,
        val javaVersion: String,
        val javaVendor: String,
        val javaHome: String,
        val jvmName: String,
        val osName: String,
        val osVersion: String,
        val osArch: String,
        val userName: String,
        val userHome: String,
        val timezone: String,
        val workDir: String,
        val pid: String?,
        val redisEnabled: Boolean,
)
