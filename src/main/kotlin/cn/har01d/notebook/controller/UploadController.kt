package cn.har01d.notebook.controller

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.ConfigService
import cn.har01d.notebook.service.QiniuService
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
class UploadController(
    private val userService: UserService,
    private val auditService: AuditService,
    private val configService: ConfigService,
    private val qiniuService: QiniuService,
) {
    private val baseDir = "upload/files"

    @PostConstruct
    fun init() {
        val file = File(baseDir)
        file.mkdirs()
    }

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): UploadResponse {
        if (!configService.get(Const.ENABLE_UPLOAD, true)) {
            throw AppForbiddenException("未开启文件上传功能")
        }

        val user = userService.requireCurrentUser()
        val prefix = IdUtils.encode(user.id!! + IdUtils.USER_OFFSET)
        val dir = File(baseDir, prefix)
        dir.mkdirs()
        val localFile = File(dir, file.originalFilename ?: file.name)
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)

        val response: UploadResponse = if (configService.get(Const.QINIU_ENABLED, false)) {
            qiniuService.uploadImage("files/${prefix}", localFile)
        } else {
            UploadResponse(localFile.name, "/files/${prefix}/" + localFile.name)
        }

        auditService.auditUpload(user, response.url)
        return response
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
