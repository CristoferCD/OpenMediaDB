package app.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/video")
@Tag(name = "Videos")
internal interface VideoController {
    @GetMapping("/{token}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Operation(summary = "Get video stream")
    fun streamVideo(@PathVariable token: String, @RequestHeader headers: HttpHeaders): ResponseEntity<ResourceRegion>
}