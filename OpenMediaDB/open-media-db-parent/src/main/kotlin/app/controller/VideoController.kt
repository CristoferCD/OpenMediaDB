package app.controller

import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/video")
internal class VideoController : BaseController() {
    @GetMapping("/{token}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun streamVideo(@PathVariable token: String): FileSystemResource {
        val videoToken = dataManagerFactory.tokenDao.get(token)!!
        val file = dataManagerFactory.fileInfoDao.get(videoToken.fileId)!!
        return FileSystemResource(file.path)
    }
}