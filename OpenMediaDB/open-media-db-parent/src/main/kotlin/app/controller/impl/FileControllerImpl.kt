package app.controller.impl

import app.controller.BaseController
import app.controller.FileController
import data.VideoFileInfo
import data.VideoToken
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

//    override fun uploadFile(showId: String, season: Int, episode: Int, file: MultipartFile): String {
//        log.info { "[uploadFile] - showId: $showId, season: $season, episode: $episode, file: ${file.originalFilename}-${file.contentType}" }
//        val show = libraryManager.getShow(showId)
//        val episodeInfo = libraryManager.getEpisode(show.imdbId, season, episode)
//        val dotIdx = file.originalFilename!!.lastIndexOf('.')
//        val extension = file.originalFilename!!.substring(dotIdx + 1)
//        val path = libraryManager.fileCrawler.importData(VideoFileInfo(
//                name = show.name,
//                season = episodeInfo.season,
//                episode = episodeInfo.episodeNumber,
//                episodeName = episodeInfo.name
//        ), extension, file.inputStream)
//        val fileId = libraryManager.insertFile(episodeInfo, path)
//        return "Created video with reference $fileId"
//    }

    override fun uploadFile(request: HttpServletRequest): String {
        val upload = ServletFileUpload()
        val iterStream = upload.getItemIterator(request)
        var showId: String? = null
        var season: String? = null
        var episode: String? = null
        var file: InputStream? = null
        var originalFilename: String? = null
        while (iterStream.hasNext()) {
            val item = iterStream.next()
                when (item.fieldName) {
                    "showId" -> showId = item.openStream().readAllBytes().decodeToString()
                    "season" -> season = item.openStream().readAllBytes().decodeToString()
                    "episode" -> episode = item.openStream().readAllBytes().decodeToString()
                    "file" -> {
                        file = item.openStream()
                        if(item is FileItemStream) {
                            originalFilename = item.name
                        }
                    }
                }
        }
        if (showId != null && season != null && episode != null && file != null && originalFilename != null) {
            val show = libraryManager.getShow(showId)
            val episodeInfo = libraryManager.getEpisode(show.imdbId, season.toInt(), episode.toInt())
            val dotIdx = originalFilename.lastIndexOf('.')
            val extension = originalFilename.substring(dotIdx + 1)
            val path = libraryManager.fileCrawler.importData(VideoFileInfo(
                    name = show.name,
                    season = episodeInfo.season,
                    episode = episodeInfo.episodeNumber,
                    episodeName = episodeInfo.name
            ), extension, file)
            val fileId = libraryManager.insertFile(episodeInfo, path)
            return "Created video with reference $fileId"
        }
        return "Error"
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