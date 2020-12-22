package cn.har01d.notebook.core.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DataAccessException
import org.springframework.web.context.request.WebRequest

@Configuration
class ExceptionConfiguration {
    @Bean
    fun errorAttributes() = object : DefaultErrorAttributes() {
        override fun getErrorAttributes(webRequest: WebRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
            val error = getError(webRequest)
            val errorAttributes = super.getErrorAttributes(webRequest, options)
            if (error is DataAccessException) {
                errorAttributes["message"] = "数据库异常"
            }
            return errorAttributes
        }
    }
}
