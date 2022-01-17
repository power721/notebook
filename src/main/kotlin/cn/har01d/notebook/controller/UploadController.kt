package cn.har01d.notebook.controller

import cn.har01d.notebook.util.copy
import org.springframework.http.MediaType
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/files")
class UploadController {
    private val baseDir = "upload/files"

    @PostConstruct
    fun init() {
        val file = File(baseDir)
        file.mkdirs()
    }

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): UploadResponse {
        val localFile = File(baseDir, file.originalFilename ?: file.name)
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)
        return UploadResponse(localFile.name, "files/" + localFile.name)
    }

    @GetMapping("/{name}")
    fun getFile(@PathVariable name: String, response: HttpServletResponse) {
        val localFile = File(baseDir, name)
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        localFile.inputStream().use {
            copy(it, response.outputStream)
        }
    }
}

data class UploadResponse(val name: String, val url: String)
