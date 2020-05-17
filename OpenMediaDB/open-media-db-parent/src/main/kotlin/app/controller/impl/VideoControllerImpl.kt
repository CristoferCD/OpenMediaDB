package app.controller.impl

import app.controller.BaseController
import app.controller.VideoController
import org.springframework.core.io.FileSystemResource
import org.springframework.web.bind.annotation.RestController

@RestController
internal class VideoControllerImpl : VideoController, BaseController() {
    override fun streamVideo(token: String): FileSystemResource {
        val videoToken = dataManagerFactory.tokenDao.get(token)!!
        val file = dataManagerFactory.fileInfoDao.get(videoToken.fileId)!!
        return FileSystemResource(file.path)
    }
}