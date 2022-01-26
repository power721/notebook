package cn.har01d.notebook.core.exception

import cn.har01d.notebook.core.Error
import cn.spark2fire.auth.exception.UserUnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DataAccessException
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.web.context.request.WebRequest

@Configuration
class ExceptionConfiguration {
    private val logger = LoggerFactory.getLogger(ExceptionConfiguration::class.java)

    @Bean
    fun errorAttributes() = object : DefaultErrorAttributes() {
        override fun getErrorAttributes(
            webRequest: WebRequest,
            options: ErrorAttributeOptions
        ): MutableMap<String, Any> {
            val error = getError(webRequest)
            val errorAttributes = super.getErrorAttributes(webRequest, options)
            errorAttributes["code"] = Error.GENERAL_ERROR.code

            when (error) {
                is DataAccessException -> {
                    errorAttributes["message"] = "数据库异常"
                }
                is HttpMessageConversionException -> {
                    errorAttributes["message"] = "数据转换异常"
                }
                is AppException -> {
                    errorAttributes["code"] = error.code
                }
                is UserUnauthorizedException -> {
                    errorAttributes["code"] = error.code
                }
            }

            if (error != null) {
                logger.warn("", error)
            } else {
                logger.warn("{}", errorAttributes["message"])
            }

            return errorAttributes
        }
    }
}
