package cn.har01d.notebook.service

import cn.har01d.notebook.entity.Captcha
import cn.har01d.notebook.entity.CaptchaRepository
import com.google.code.kaptcha.impl.DefaultKaptcha
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.time.Instant
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional

@Service
class CaptchaService(private val repository: CaptchaRepository, private val kaptchaProducer: DefaultKaptcha) {
    @Transactional
    fun generate(name: String, response: HttpServletResponse) {
        val code = kaptchaProducer.createText()
        val image: BufferedImage = kaptchaProducer.createImage(code)
        repository.deleteAllByName(name)
        repository.save(Captcha(name, code, Instant.now().plusSeconds(60)))
        response.contentType = MediaType.IMAGE_JPEG_VALUE
        ImageIO.write(image, "jpg", response.outputStream)
    }

    fun validate(name: String, code: String?): Boolean {
        val captcha = repository.findByName(name) ?: return false

        if (Instant.now().isAfter(captcha.expireTime)) {
            repository.delete(captcha)
            return false
        }

        if (captcha.code == code) {
            repository.delete(captcha)
            return true
        }

        return false
    }
}
