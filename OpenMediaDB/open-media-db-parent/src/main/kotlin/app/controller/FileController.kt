package app.controller

import data.VideoFileInfo
import data.VideoToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest
import java.time.ZonedDateTime

@RestController
@RequestMapping("/files")
@Tag(name = "Files", description = "File management")
internal class FileController : BaseController() {

    @GetMapping("/{id}")
    @Operation(summary = "Get file token", description = "Returns token to consume file without authorization headers")
    fun getFile(@Parameter(description = "File id") @PathVariable id: Int): String {
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

    @PostMapping
    @Operation(summary = "Upload a file", description = "Upload a file")
    fun uploadFile(@RequestParam showId: String, @RequestParam season: Int, @RequestParam episode: Int, @RequestParam file: MultipartFile): String {
        log.info { "[uploadFile] - showId: $showId, season: $season, episode: $episode, file: ${file.originalFilename}-${file.contentType}" }
        val show = libraryManager.getShow(showId)
        val episodeInfo = libraryManager.getEpisode(show.imdbId, season, episode)
        val dotIdx = file.originalFilename!!.lastIndexOf('.')
        val extension = file.originalFilename!!.substring(dotIdx + 1)
        val path = libraryManager.fileCrawler.importData(VideoFileInfo(
                name = show.name,
                season = episodeInfo.season,
                episode = episodeInfo.episodeNumber,
                episodeName = episodeInfo.name
        ), extension, file.inputStream)
        val fileId = libraryManager.insertFile(episodeInfo, path)
        return "Created video with reference $fileId"
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a file", description = "Delete a file")
    fun deleteFile(@PathVariable id: Int) {
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