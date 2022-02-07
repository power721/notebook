package cn.har01d.notebook.controller

import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.entity.User
import cn.har01d.notebook.service.AuditService
import cn.har01d.notebook.service.ConfigService
import cn.har01d.notebook.service.QiniuService
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
class ImageController(
    private val userService: UserService,
    private val auditService: AuditService,
    private val configService: ConfigService,
    private val qiniuService: QiniuService,
) {
    private val baseDir = "upload/images"

    @PostConstruct
    fun init() {
        val file = File(baseDir)
        file.mkdirs()
    }

    @PostMapping("/multiple")
    fun uploadImages(@RequestParam(value = "file") files: List<MultipartFile>): List<UploadResponse> {
        if (!configService.get(Const.ENABLE_IMAGE_UPLOAD, true)) {
            throw AppForbiddenException("未开启图片上传功能")
        }

        val user = userService.requireCurrentUser()
        return files.map { uploadImage(user, it) }
    }

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): UploadResponse {
        if (!configService.get(Const.ENABLE_IMAGE_UPLOAD, true)) {
            throw AppForbiddenException("未开启图片上传功能")
        }

        val user = userService.requireCurrentUser()
        return uploadImage(user, file)
    }

    private fun uploadImage(user: User, file: MultipartFile): UploadResponse {
        val prefix = IdUtils.encode(user.id!! + IdUtils.USER_OFFSET)
        val dir = File(baseDir, prefix)
        dir.mkdirs()
        val localFile = File(dir, generateFileName())
        localFile.createNewFile()
        FileCopyUtils.copy(file.bytes, localFile)

        val response = if (configService.get(Const.QINIU_ENABLED, false)) {
            qiniuService.uploadImage("images/${prefix}", localFile)
        } else {
            UploadResponse(localFile.name, "/images/${prefix}/" + localFile.name)
        }

        auditService.auditUpload(user, response.url)
        return response
    }

    @GetMapping("/{prefix}/{name}")
    fun getImage(@PathVariable prefix: String, @PathVariable name: String, response: HttpServletResponse) {
        val file = File("$baseDir/$prefix", name)
        if (!file.exists()) {
            throw AppNotFoundException("图片不存在")
        }
        response.contentType = MediaType.IMAGE_PNG_VALUE
        response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
        file.inputStream().use {
            copy(it, response.outputStream)
        }
    }
}
