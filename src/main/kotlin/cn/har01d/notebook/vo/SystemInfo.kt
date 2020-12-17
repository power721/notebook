package cn.har01d.notebook.vo

class SystemInfo(
        var ip: String?,
        var hostName: String?,
        var jvmTotalMemory: Long,
        var jvmFreeMemory: Long,
        var jvmCpus: Int,
        var javaVersion: String,
        var javaVendor: String,
        var javaHome: String,
        var jvmName: String,
        var osName: String,
        var osVersion: String,
        var osArch: String,
        var userName: String,
        var userHome: String,
        var timezone: String,
        var workDir: String,
        var pid: String?,
)
