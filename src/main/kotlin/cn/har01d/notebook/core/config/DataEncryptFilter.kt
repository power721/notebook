package cn.har01d.notebook.core.config

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.service.ConfigService
import cn.har01d.notebook.service.EncryptService
import com.fasterxml.jackson.databind.ObjectMapper
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.multipart.MultipartResolver
import java.io.*
import java.security.Security
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class DataEncryptFilter(
    private val objectMapper: ObjectMapper,
    private val configService: ConfigService,
    private val encryptService: EncryptService,
    private val multipartResolver: MultipartResolver,
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(DataEncryptFilter::class.java)

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = System.currentTimeMillis()
        val sign = request.getHeader("sign") ?: ""
        val time = request.getHeader("time") ?: ""
        val requestWrapper = wrapper(request)

        if (request.getHeader("encrypted") == "true") {
            val data = objectMapper.readValue(requestWrapper.body, RequestDataWrapper::class.java)
            requestWrapper.body = encryptService.decrypt(data.data, sign, time)
        } else {
//            val uri = request.requestURI + if (request.queryString != null) "?" + request.queryString else ""
//            if (encryptService.sign(uri, time) != sign) {
//                logger.warn("请求校验失败 $uri")
//            }
        }

        val responseWrapper = ResponseWrapper(response)

        filterChain.doFilter(requestWrapper, responseWrapper)

        response.setIntHeader("x-process-time", (System.currentTimeMillis() - start).toInt())
        response.outputStream.write(responseWrapper.data)
    }

    private fun wrapper(request: HttpServletRequest): RequestWrapper {
        val contentType = request.contentType
        val req = if (contentType != null && contentType.contains("multipart/")) {
            multipartResolver.resolveMultipart(request)
        } else request
        return RequestWrapper(req)
    }

    data class RequestDataWrapper(val data: String)

    class FilterServletOutputStream(output: OutputStream) : ServletOutputStream() {
        var output: DataOutputStream = DataOutputStream(output)

        override fun isReady() = true

        override fun setWriteListener(listener: WriteListener) {
        }

        override fun write(arg0: Int) {
            output.write(arg0)
        }

        override fun write(arg0: ByteArray, arg1: Int, arg2: Int) {
            output.write(arg0, arg1, arg2)
        }

        override fun write(arg0: ByteArray) {
            output.write(arg0)
        }
    }

    class ResponseWrapper(response: HttpServletResponse) : HttpServletResponseWrapper(response) {
        var output: ByteArrayOutputStream = ByteArrayOutputStream()
        var filterOutput: FilterServletOutputStream? = null

        override fun getOutputStream(): ServletOutputStream {
            if (filterOutput == null) {
                filterOutput = FilterServletOutputStream(output)
            }
            return filterOutput as FilterServletOutputStream
        }

        override fun getWriter(): PrintWriter {
            return PrintWriter(filterOutput!!)
        }

        val data: ByteArray
            get() = output.toByteArray()
    }

    class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
        var body: String

        init {
            val stringBuilder = StringBuilder()
            BufferedReader(InputStreamReader(request.inputStream)).use {
                var line = it.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = it.readLine()
                }
            }
            body = stringBuilder.toString()
        }

        override fun getInputStream(): ServletInputStream {
            val byteArrayInputStream = ByteArrayInputStream(body.toByteArray())
            return object : ServletInputStream() {
                override fun read() = byteArrayInputStream.read()

                override fun isFinished() = false

                override fun isReady() = false

                override fun setReadListener(listener: ReadListener) {
                }
            }
        }

        override fun getReader(): BufferedReader {
            return BufferedReader(InputStreamReader(inputStream))
        }
    }
}
