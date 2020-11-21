package app.controller.impl

import app.controller.BaseController
import app.controller.FileController
import data.VideoFileInfo
import data.VideoToken
import data.request.UploadFileRB
import org.apache.tomcat.util.http.fileupload.FileItemStream
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.security.MessageDigest
import java.time.ZonedDateTime
import javax.servlet.http.HttpServletRequest


@RestController
internal class FileControllerImpl : FileController, BaseController() {
    override fun getFile(id: Int): String {
        val file = dataManagerFactory.fileInfoDao.get(id)!!
        val tokenStr = file.path.toString().sha512Token()
        val existingToken = dataManagerFactory.tokenDao.get(tokenStr)
        return if (existingToken != null) {
            dataManagerFactory.tokenDao.update(
                    existingToken.copy(expires = ZonedDateTime.now().plusDays(1)))
            existingToken.token
        } else {
            val token = VideoToken(
                    fileId = file.id!!,
                    token = tokenStr,
                    expires = ZonedDateTime.now().plusDays(1)
            )
            dataManagerFactory.tokenDao.insert(token)
            token.token
        }
    }

    override fun uploadFile(request: HttpServletRequest): String {
        parseUploadFileRequest(request)?.let {
            val show = libraryManager.getShow(it.showId!!)
            val episodeInfo = libraryManager.getEpisode(show.imdbId, it.season!!.toInt(), it.episode!!.toInt())
            val dotIdx = it.originalFilename!!.lastIndexOf('.')
            val extension = it.originalFilename!!.substring(dotIdx + 1)
            val path = libraryManager.fileCrawler.importData(VideoFileInfo(
                    name = show.name,
                    season = episodeInfo.season,
                    episode = episodeInfo.episodeNumber,
                    episodeName = episodeInfo.name
            ), extension, it.file!!)
            val fileId = libraryManager.insertFile(episodeInfo, path)
            return "Created video with reference $fileId"
        }
        return "Error"
    }

    private fun parseUploadFileRequest(request: HttpServletRequest): UploadFileRB? {
        val result = UploadFileRB()
        val upload = ServletFileUpload()
        val iterStream = upload.getItemIterator(request)
        while (iterStream.hasNext()) {
            val item = iterStream.next()
            when (item.fieldName) {
                "showId" -> result.showId = item.openStream().readAllBytes().decodeToString()
                "season" -> result.season = item.openStream().readAllBytes().decodeToString()
                "episode" -> result.episode = item.openStream().readAllBytes().decodeToString()
                "file" -> {
                    result.file = item.openStream()
                    if(item is FileItemStream) {
                        result.originalFilename = item.name
                    }
                }
            }
        }
        return if (result.isComplete()) result else null
    }

    override fun deleteFile(id: Int) {
        dataManagerFactory.fileInfoDao.delete(id)
    }

    private fun String.sha512Token(): String {
        val hexChars = "0123456789ABCDEF"
        val bytes = MessageDigest.getInstance("SHA-512")
                .digest(this.toByteArray())
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach {
            val i = it.toInt()
            result.append(hexChars[i shr 4 and 0x0f])
            result.append(hexChars[i and 0x0f])
        }
        return result.toString()
    }
}