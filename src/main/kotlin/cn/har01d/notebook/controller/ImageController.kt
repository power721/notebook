package cn.har01d.notebook.controller

import cn.har01d.notebook.util.copy
import cn.har01d.notebook.util.generateFileName
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/images")
class ImageController {
    private val baseDir = "upload/images"

    @PostConstruct
    fun init() {
        val file = File(baseDir)
        file.mkdirs()
    }

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): Map<String, String> {
        val localFile = File(baseDir, generateFileName())
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)
        return mapOf("location" to "/images/" + localFile.name)
    }

    @GetMapping("/{name}")
    fun getFile(@PathVariable name: String, response: HttpServletResponse) {
        val localFile = File(baseDir, name)
        response.contentType = MediaType.IMAGE_PNG_VALUE
        response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
        localFile.inputStream().use {
            copy(it, response.outputStream)
        }
    }
}
