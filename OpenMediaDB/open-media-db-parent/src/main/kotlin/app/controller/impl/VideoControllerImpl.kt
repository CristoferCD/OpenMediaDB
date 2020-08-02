package app.controller.impl

import app.controller.BaseController
import app.controller.VideoController
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.web.bind.annotation.RestController
import kotlin.math.min

@RestController
internal class VideoControllerImpl : VideoController, BaseController() {
    override fun streamVideo(token: String, headers: HttpHeaders): ResponseEntity<ResourceRegion> {
        val videoToken = dataManagerFactory.tokenDao.get(token)!!
        val file = dataManagerFactory.fileInfoDao.get(videoToken.fileId)!!
        val video = FileSystemResource(file.path)
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion(video, headers.range.firstOrNull()))
    }

    private fun resourceRegion(video: FileSystemResource, range: HttpRange?): ResourceRegion {
        val contentLength = video.contentLength()
        return if (range != null) {
            val start = range.getRangeStart(contentLength)
            val end = range.getRangeEnd(contentLength)
            val length = min(1000000L, end - start + 1)
            ResourceRegion(video, start, length)
        } else {
            val length = min(1000000L, contentLength)
            ResourceRegion(video, 0, length)
        }
    }
}