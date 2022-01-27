package cn.har01d.notebook.core.exception

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.service.ConfigService
import cn.har01d.notebook.service.EncryptService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ErrorResponseBodyAdvice(
    private val objectMapper: ObjectMapper,
    private val configService: ConfigService,
    private val encryptService: EncryptService,
) : ResponseBodyAdvice<Any?> {
    private val specialPaths = arrayOf("/accounts/info", "/config/site", "/config/menus")

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val enableEncrypt = configService.get(Const.ENABLE_ENCRYPT, false)
        if (body != null && enableEncrypt) {
            val special = specialPaths.contains(request.uri.path)
            val sign = request.headers.getFirst("sign") ?: ""
            val time = request.headers.getFirst("time") ?: ""
            val encryptedData = encryptService.encrypt(special, objectMapper.writeValueAsBytes(body), sign, time)
            response.headers["sign"] = encryptService.sign(encryptedData, Const.DEVELOPER)
            response.headers["x-content-type"] = selectedContentType.toString()
            response.headers["content-type"] = MediaType.TEXT_PLAIN_VALUE
            return encryptedData
        }
        return body
    }
}
