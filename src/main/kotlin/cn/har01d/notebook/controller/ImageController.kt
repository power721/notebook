package cn.har01d.notebook.controller

import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.UserService
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.copy
import cn.har01d.notebook.util.generateFileName
import cn.har01d.notebook.vo.UploadResponse
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
class ImageController(private val userService: UserService, private val auditService: AuditService) {
    private val baseDir = "upload/images"

    @PostConstruct
    fun init() {
        val file = File(baseDir)
        file.mkdirs()
    }

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): UploadResponse {
        val user = userService.requireCurrentUser()
        val prefix = IdUtils.encode(user.id!! + IdUtils.USER_OFFSET)
        val dir = File(baseDir, prefix)
        dir.mkdirs()
        val localFile = File(dir, generateFileName())
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)
        val url = "/images/${prefix}/" + localFile.name
        auditService.auditUpload(user, url)
        return UploadResponse(file.originalFilename ?: file.name, url)
    }

    @GetMapping("/{prefix}/{name}")
    fun getFile(@PathVariable prefix: String, @PathVariable name: String, response: HttpServletResponse) {
        val localFile = File("$baseDir/$prefix", name)
        response.contentType = MediaType.IMAGE_PNG_VALUE
        response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
        localFile.inputStream().use {
            copy(it, response.outputStream)
        }
    }
}
