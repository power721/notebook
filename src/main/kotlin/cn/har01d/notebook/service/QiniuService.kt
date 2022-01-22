package cn.har01d.notebook.service

import cn.har01d.notebook.vo.UploadResponse
import com.qiniu.common.QiniuException
import com.qiniu.http.Response
import com.qiniu.storage.Configuration
import com.qiniu.storage.Region
import com.qiniu.storage.UploadManager
import com.qiniu.util.Auth
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class QiniuService(private val configService: ConfigService) {
    private val log = LoggerFactory.getLogger(QiniuService::class.java)

    fun uploadImage(name: String, file: File): UploadResponse {
        val qiniuProperties = configService.getQiniuProperties()
        val cfg = Configuration()
        val uploadManager = UploadManager(cfg)
        val auth: Auth = Auth.create(qiniuProperties.accessKey, qiniuProperties.secretKey)
        val upToken: String = auth.uploadToken(qiniuProperties.bucket)
        try {
            val response: Response = uploadManager.put(file, name, upToken)
            if (!response.isOK) {
                log.warn("上传图片{}失败: {}", name, response.error)
            } else {
                log.info("上传图片{}成功", name)
            }
        } catch (e: QiniuException) {
            throw RuntimeException("上传图片失败", e)
        }

        file.delete()

        val url = qiniuProperties.domain + "/" + name
        return UploadResponse(name, url)
    }

}
