package cn.har01d.notebook.core.config

import com.google.code.kaptcha.impl.DefaultKaptcha
import com.google.code.kaptcha.util.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class KaptchaConfig {
    @Bean
    fun producer(): DefaultKaptcha {
        val properties = Properties()
        properties.setProperty("kaptcha.border.color", "0,128,128")
        properties.setProperty("kaptcha.textproducer.font.color", "14,110,184")
        properties.setProperty("kaptcha.image.width", "92")
        properties.setProperty("kaptcha.image.height", "46")
        properties.setProperty("kaptcha.textproducer.font.size", "34")
        properties.setProperty("kaptcha.textproducer.char.length", "4")

        val config = Config(properties)
        val defaultKaptcha = DefaultKaptcha()
        defaultKaptcha.config = config
        return defaultKaptcha
    }
}
