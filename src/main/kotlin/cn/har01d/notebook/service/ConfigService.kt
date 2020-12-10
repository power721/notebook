package cn.har01d.notebook.service

import cn.har01d.notebook.entity.Config
import cn.har01d.notebook.entity.ConfigRepository
import cn.har01d.notebook.entity.ConfigType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ConfigService(private val repository: ConfigRepository) {
    fun findAll(): List<Config> = repository.findAll()

    fun get(name: String) = repository.findByIdOrNull(name)

    fun get(name: String, default: String): String {
        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default))
        }
        return config.value
    }

    fun get(name: String, default: Boolean): Boolean {
        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.BOOLEAN))
        }
        return config.value == "true"
    }

    fun get(name: String, default: Int): Int {
        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.INT))
        }
        return config.value.toInt()
    }

    fun get(name: String, default: Long): Long {
        var config = repository.findByIdOrNull(name)
        if (config == null) {
            config = repository.save(Config(name, default.toString(), ConfigType.LONG))
        }
        return config.value.toLong()
    }

    fun save(config: Config): Config {
        return repository.save(config)
    }

    fun save(name: String, value: String): Config {
        return repository.save(Config(name, value))
    }

    fun save(name: String, value: Boolean): Config {
        return repository.save(Config(name, value.toString(), ConfigType.BOOLEAN))
    }

    fun save(name: String, value: Int): Config {
        return repository.save(Config(name, value.toString(), ConfigType.INT))
    }

    fun save(name: String, value: Long): Config {
        return repository.save(Config(name, value.toString(), ConfigType.LONG))
    }
}
