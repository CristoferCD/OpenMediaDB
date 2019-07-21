package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.FileInfo
import data.VideoFileInfo
import data.VideoToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest
import java.time.ZonedDateTime

@RestController
@RequestMapping("/files")
class FileController: BaseController() {

    @GetMapping("/{id}")
    fun getFile(@PathVariable id: Int): String {
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
    fun uploadFile(@RequestParam showId: String, @RequestParam season: Int, @RequestParam episode: Int, @RequestParam file: MultipartFile): String {
        log.info { "[uploadFile] - showId: $showId, season: $season, episode: $episode, file: ${file.originalFilename}-${file.contentType}" }
        val show = LibraryManager.getOrCreateShow(showId)
        val episodeInfo = LibraryManager.getOrCreateEpisode(show, season, episode)
        val dotIdx = file.originalFilename!!.lastIndexOf('.')
        val extension = file.originalFilename!!.substring(dotIdx + 1)
        val path = LibraryManager.fileCrawler.importData(VideoFileInfo(
                name = show.name,
                season = episodeInfo.season.toString(),
                episode = episodeInfo.episodeNumber.toString(),
                episodeName = episodeInfo.name
        ), extension, file.inputStream)
        val fileId = LibraryManager.insertFile(episodeInfo, path)
        return "Created video with reference $fileId"
    }

//    @PostMapping
//    fun uploadFile(req: HttpServletRequest) {
//        try {
//            if (!ServletFileUpload.isMultipartContent(req))
//                throw ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Must be a multipart request")
//
//            val showId = req.getParameter("showId")
//            val season = req.getParameter("season")
//            val episode = req.getParameter("episode")
//            log.info { "Got parameters: [$showId] ${season}x$episode" }
//            val iter = ServletFileUpload().getItemIterator(req)
//            while(iter.hasNext()) {
//                val item = iter.next()
//                val name = item.fieldName
//                log.info { "Item name: $name (${item.isFormField}" }
//            }
//        } catch (e: FileUploadException) {
//            log.error { "File upload error $e" }
//        } catch (e: IOException) {
//            log.error { "Internal server IO error $e" }
//        }
//    }

//    @PostMapping
//    fun uploadFile(@RequestParam file: MultipartFile): String {
//        println("Received ${file.originalFilename} and type ${file.contentType}")
//        val dotIdx = file.originalFilename!!.lastIndexOf('.')
//        val nameWithoutExtension = file.originalFilename!!.substring(0, dotIdx)
//        val extension = file.originalFilename!!.substring(dotIdx + 1)
//        try {
//            val videoInfo = LibraryManager.fileCrawler.parseFileName(nameWithoutExtension)
//            val show = LibraryManager.getOrCreateShowByName(videoInfo.name)
//            val path = LibraryManager.fileCrawler.importData(videoInfo, extension, file.bytes)
//            val fileId = dataManagerFactory.fileInfoDao.insert(FileInfo(
//                    id = null,
//                    codec = "",
//                    bitrate = "",
//                    resolution = "",
//                    duration = null,
//                    path = path
//            ))
//            val videoLocally = dataManagerFactory.videoDao.findFromParent(show.imdbId, videoInfo.season.toInt(), videoInfo.episode.toInt())
//            if (videoLocally.isEmpty()) {
//                val tmdbEpisode = TMDbManager.apiAccess.tvEpisodes.getEpisode(show.externalIds.tmdb!!,
//                        videoInfo.season.toInt(), videoInfo.episode.toInt(), "en", TmdbTvEpisodes.EpisodeMethod.external_ids)
//                dataManagerFactory.videoDao.insert(Video(
//                        id = null,
//                        showId = show.imdbId,
//                        imdbId = tmdbEpisode.externalIds.imdbId,
//                        sinopsis = tmdbEpisode.overview,
//                        name = tmdbEpisode.name,
//                        episodeNumber = tmdbEpisode.episodeNumber,
//                        season = tmdbEpisode.seasonNumber,
//                        imgPoster = null,
//                        fileId = fileId,
//                        externalIds = ExternalIds(imdb = tmdbEpisode.externalIds?.imdbId,
//                                tvdb = tmdbEpisode.externalIds?.tvdbId?.toInt(),
//                                tmdb = tmdbEpisode.id)
//                ))
//            } else {
//                val local = videoLocally.first()
//                local.fileId = fileId
//                dataManagerFactory.videoDao.update(local)
//            }
//
//            return "Successfully imported"
//        } catch (e: FileParseException) {
//            return e.message ?: "Error"
//        }
//    }

    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Int) {
        dataManagerFactory.fileInfoDao.delete(id)
    }

    private fun String.sha512Token(): String {
        val hex_chars = "0123456789ABCDEF"
        val bytes = MessageDigest.getInstance("SHA-512")
                .digest(this.toByteArray())
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach {
            val i = it.toInt()
            result.append(hex_chars[i shr 4 and 0x0f])
            result.append(hex_chars[i and 0x0f])
        }
        return result.toString()
    }
}