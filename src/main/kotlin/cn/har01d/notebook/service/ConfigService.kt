package cn.har01d.notebook.service

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.core.config.QiniuProperties
import cn.har01d.notebook.entity.Config
import cn.har01d.notebook.entity.ConfigRepository
import cn.har01d.notebook.entity.ConfigType
import com.github.benmanes.caffeine.cache.Cache
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ConfigService(private val repository: ConfigRepository, cacheService: CacheService) {
    private val cache: Cache<String, String> = cacheService.localCache()

    fun findAll(): List<Config> = repository.findAll()

    fun getSiteConfig(admin: Boolean = false): SiteConfig {
        return SiteConfig(
            get(Const.SITE_NAME, "Notebook"),
            get(Const.BRAND_COLOR, "teal"),
            get(Const.QR_CODE, ""),
            get(Const.ICP_BEIAN, ""),
            get(Const.GOV_BEIAN, ""),
            get(Const.SHOW_VIEWS, true),
            get(Const.SHOW_WORDS, true),
            get(Const.ENABLE_AUDIT, true),
            get(Const.ENABLE_COMMENT, true),
            get(Const.ENABLE_FILE_UPLOAD, true),
            get(Const.ENABLE_IMAGE_UPLOAD, true),
            get(Const.ENABLE_SIGNUP, true),
            get(Const.ENABLE_HEARTBEAT, true),
            get(Const.ENABLE_ENCRYPT, false),
            get(Const.SECRET_KEY, ""),
            if (admin) getQiniuProperties() else QiniuProperties(),
        )
    }

    fun getQiniuProperties() = QiniuProperties(
        get(Const.QINIU_ENABLED, false),
        get(Const.QINIU_ACCESS_KEY, ""),
        get(Const.QINIU_SECRET_KEY, ""),
        get(Const.QINIU_BUCKET, ""),
        get(Const.QINIU_DOMAIN, ""),
    )

    fun updateSiteConfig(dto: SiteConfig): SiteConfig {
        save(Const.SITE_NAME, dto.siteName)
        save(Const.BRAND_COLOR, dto.brandColor)
        save(Const.QR_CODE, dto.qrCode)
        save(Const.ICP_BEIAN, dto.icpBeian)
        save(Const.GOV_BEIAN, dto.govBeian)
        save(Const.SHOW_VIEWS, dto.showViews)
        save(Const.SHOW_WORDS, dto.showWords)
        save(Const.ENABLE_AUDIT, dto.enableAudit)
        save(Const.ENABLE_COMMENT, dto.enableComment)
        save(Const.ENABLE_FILE_UPLOAD, dto.enableFileUpload)
        save(Const.ENABLE_IMAGE_UPLOAD, dto.enableImageUpload)
        save(Const.ENABLE_SIGNUP, dto.enableSignup)
        save(Const.ENABLE_HEARTBEAT, dto.enableHeartbeat)
        save(Const.ENABLE_ENCRYPT, dto.enableEncrypt)
        saveQiniuProperties(dto.qiniu)
        return getSiteConfig(true)
    }

    fun saveQiniuProperties(qiniu: QiniuProperties) {
        save(Const.QINIU_ENABLED, qiniu.enabled)
        save(Const.QINIU_ACCESS_KEY, qiniu.accessKey)
        save(Const.QINIU_SECRET_KEY, qiniu.secretKey)
        save(Const.QINIU_BUCKET, qiniu.bucket)
        save(Const.QINIU_DOMAIN, qiniu.domain)
    }

    fun get(name: String) = repository.findByIdOrNull(name)

    fun get(name: String, default: String): String {
        val value = cache.getIfPresent(name)
        if (value != null) {
            return value
        }

        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default))
        }
        cache.put(name, config.value)
        return config.value
    }

    fun get(name: String, default: Boolean): Boolean {
        val value = cache.getIfPresent(name)
        if (value != null) {
            return value.toBoolean()
        }

        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.BOOLEAN))
        }
        cache.put(name, config.value)
        return config.value == "true"
    }

    fun get(name: String, default: Int): Int {
        val value = cache.getIfPresent(name)
        if (value != null) {
            return value.toInt()
        }

        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.INT))
        }
        cache.put(name, config.value)
        return config.value.toInt()
    }

    fun get(name: String, default: Long): Long {
        val value = cache.getIfPresent(name)
        if (value != null) {
            return value.toLong()
        }

        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.LONG))
        }
        cache.put(name, config.value)
        return config.value.toLong()
    }

    fun save(config: Config): Config {
        cache.put(config.name, config.value)
        return repository.save(config)
    }

    fun save(name: String, value: String): Config {
        cache.put(name, value)
        return repository.save(Config(name, value))
    }

    fun save(name: String, value: Boolean): Config {
        cache.put(name, value.toString())
        return repository.save(Config(name, value.toString(), ConfigType.BOOLEAN))
    }

    fun save(name: String, value: Int): Config {
        cache.put(name, value.toString())
        return repository.save(Config(name, value.toString(), ConfigType.INT))
    }

    fun save(name: String, value: Long): Config {
        cache.put(name, value.toString())
        return repository.save(Config(name, value.toString(), ConfigType.LONG))
    }
}
