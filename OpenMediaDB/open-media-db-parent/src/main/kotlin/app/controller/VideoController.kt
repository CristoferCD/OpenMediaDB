package app.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/video")
@Tag(name = "Videos")
internal interface VideoController {
    @GetMapping("/{token}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Operation(summary = "Get video stream")
    fun streamVideo(@PathVariable token: String): FileSystemResource
}