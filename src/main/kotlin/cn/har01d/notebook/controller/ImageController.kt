package cn.har01d.notebook.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.postForObject
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/images")
class ImageController(restTemplateBuilder: RestTemplateBuilder, private val mapper: ObjectMapper) {
    private val restTemplate = restTemplateBuilder.build()

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): Map<String, String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val parts: MultiValueMap<String, Any> = LinkedMultiValueMap()
        parts.add("photo", file.resource)
        val httpEntity = HttpEntity(parts, headers)
        val json = restTemplate.postForObject<String>("https://mp.toutiao.com/upload_photo/?type=json", httpEntity)
        val image = mapper.readValue(json, ImageResponse::class.java)
        return mapOf("location" to image.web_url)
    }
}

data class ImageResponse(val width: Int, val height: Int, val message: String, val web_url: String, val web_uri: String)
