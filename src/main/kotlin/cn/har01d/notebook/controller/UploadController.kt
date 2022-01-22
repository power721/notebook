package cn.har01d.notebook.controller

import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.UserService
import cn.har01d.notebook.util.IdUtils
import cn.har01d.notebook.util.copy
import cn.har01d.notebook.vo.UploadResponse
import org.springframework.http.MediaType
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/files")
class UploadController(private val userService: UserService, private val auditService: AuditService) {
    private val baseDir = "upload/files"

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
        val localFile = File(dir, file.originalFilename ?: file.name)
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)
        val url = "/files/${prefix}/" + localFile.name
        auditService.auditUpload(user, url)
        return UploadResponse(localFile.name, url)
    }

    @GetMapping("/{prefix}/{name}")
    fun getFile(@PathVariable prefix: String, @PathVariable name: String, response: HttpServletResponse) {
        val localFile = File("$baseDir/$prefix", name)
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        localFile.inputStream().use {
            copy(it, response.outputStream)
        }
    }
}
