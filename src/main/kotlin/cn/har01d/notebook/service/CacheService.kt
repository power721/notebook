package cn.har01d.notebook.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Policy
import com.github.benmanes.caffeine.cache.stats.CacheStats
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import java.util.function.Function
import javax.annotation.PostConstruct

@Service
class CacheService(private val redisTemplate: StringRedisTemplate) {
    private val logger = LoggerFactory.getLogger(CacheService::class.java)
    private var redisEnabled: Boolean = true

    @PostConstruct
    fun init() {
        try {
            redisTemplate.hasKey("")
            logger.info("Use Redis Cache.")
        } catch (e: RedisConnectionFailureException) {
            logger.warn("Cannot connect to Redis server, use local memory cache.")
            redisEnabled = false
        }
    }

    fun cache(name: String, expiration: Duration? = null, maximumSize: Long? = null): Cache<String, String> {
        return if (redisEnabled) {
            StringRedisCache(name, redisTemplate, expiration, maximumSize)
        } else {
            localCache(name, expiration, maximumSize)
        }
    }

    fun intCache(name: String, expiration: Duration? = null, maximumSize: Long? = null): Cache<String, Int> {
        return if (redisEnabled) {
            IntRedisCache(name, redisTemplate, expiration, maximumSize)
        } else {
            localCache(name, expiration, maximumSize)
        }
    }

    fun boolCache(name: String, expiration: Duration? = null, maximumSize: Long? = null): Cache<String, Boolean> {
        return if (redisEnabled) {
            BoolRedisCache(name, redisTemplate, expiration, maximumSize)
        } else {
            localCache(name, expiration, maximumSize)
        }
    }

    fun <String, V> localCache(
        name: String,
        expiration: Duration? = null,
        maximumSize: Long? = null
    ): Cache<String, V> {
        val builder = Caffeine.newBuilder()
        if (maximumSize != null) {
            builder.maximumSize(maximumSize)
        }
        if (expiration != null) {
            builder.expireAfterWrite(expiration.toMillis(), TimeUnit.MILLISECONDS)
        }
        return builder.build()
    }
}

abstract class RedisCache<V>(
    name: String,
    private val redisTemplate: StringRedisTemplate,
    private val expiration: Duration? = null,
    private val maximumSize: Long? = null,
) : Cache<String, V> {
    private val op = redisTemplate.opsForValue()
    private val prefix = "Notebook:$name"

    private fun internalKey(key: Any) = "$prefix:$key"

    override fun getIfPresent(key: Any): V? {
        return op.get(internalKey(key))?.let {
            valueFromString(it)
        }
    }

    override fun get(key: String, mappingFunction: Function<in String, out V>): V? {
        return if (redisTemplate.hasKey(internalKey(key))) {
            getIfPresent(key)
        } else {
            val value = mappingFunction.apply(key)
            put(key, value)
            value
        }
    }

    override fun getAllPresent(keys: MutableIterable<*>): MutableMap<String, V> {
        val map = mutableMapOf<String, V>()
        for (key in keys) {
            if (key != null && redisTemplate.hasKey(internalKey(key))) {
                val value = getIfPresent(key)
                if (value != null) {
                    map[key.toString()] = value
                }
            }
        }
        return map
    }

    override fun put(key: String, value: V) {
        op.set(internalKey(key), valueToString(value))
        if (expiration != null) {
            redisTemplate.expire(internalKey(key), expiration)
        }
    }

    override fun putAll(map: MutableMap<out String, out V>) {
        for (entry in map) {
            put(entry.key, entry.value)
        }
    }

    override fun invalidate(key: Any) {
        redisTemplate.delete(internalKey(key))
    }

    override fun invalidateAll(keys: MutableIterable<*>) {
        for (key in keys) {
            if (key != null) {
                invalidate(key)
            }
        }
    }

    override fun invalidateAll() {
        invalidateAll(redisTemplate.keys("$prefix:*"))
    }

    override fun estimatedSize(): Long {
        return redisTemplate.keys("$prefix:*").size.toLong()
    }

    override fun stats() = CacheStats.empty()

    override fun asMap(): ConcurrentMap<String, V> {
        val map = ConcurrentHashMap<String, V>()
        for (key in redisTemplate.keys("$prefix:*")) {
            val value = getIfPresent(key)
            if (value != null) {
                map[key.toString().replace("$prefix:", "")] = value
            }
        }
        return map
    }

    override fun cleanUp() {
    }

    override fun policy(): Policy<String, V> {
        TODO("Not yet implemented")
    }

    fun valueToString(value: V): String = value.toString()

    abstract fun valueFromString(value: String): V
}

class StringRedisCache(
    name: String,
    redisTemplate: StringRedisTemplate,
    expiration: Duration? = null,
    maximumSize: Long? = null,
) : RedisCache<String>(name, redisTemplate, expiration, maximumSize) {
    override fun valueFromString(value: String) = value
}

class IntRedisCache(
    name: String,
    redisTemplate: StringRedisTemplate,
    expiration: Duration? = null,
    maximumSize: Long? = null,
) : RedisCache<Int>(name, redisTemplate, expiration, maximumSize) {
    override fun valueFromString(value: String) = value.toInt()
}

class BoolRedisCache(
    name: String,
    redisTemplate: StringRedisTemplate,
    expiration: Duration? = null,
    maximumSize: Long? = null,
) : RedisCache<Boolean>(name, redisTemplate, expiration, maximumSize) {
    override fun valueFromString(value: String) = value.toBoolean()
}
