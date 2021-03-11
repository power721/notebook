package cn.har01d.notebook.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
class ImageController(restTemplateBuilder: RestTemplateBuilder) {
    private val restTemplate = restTemplateBuilder.build()
    private val mapper = jacksonObjectMapper()
    private val apiUrl = "http://changyan.sohu.com/api/2/comment/attachment"

    @PostMapping
    fun upload(@RequestParam(value = "file") file: MultipartFile): Map<String, String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val parts: MultiValueMap<String, Any> = LinkedMultiValueMap()
        parts.add("file", file.resource)
        val httpEntity = HttpEntity(parts, headers)
        val json = restTemplate.postForObject<String>(apiUrl, httpEntity)
        val image = mapper.readValue<ImageResponse>(json.substring(1, json.length - 1).replace("\\", ""))
        return mapOf("location" to image.url)
    }
}

data class ImageResponse(val url: String)
