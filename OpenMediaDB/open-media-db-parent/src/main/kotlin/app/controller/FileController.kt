package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.ExternalIds
import data.FileInfo
import data.Video
import data.VideoFileInfo
import data.tmdb.TMDbManager
import exceptions.FileParseException
import info.movito.themoviedbapi.TmdbTvEpisodes
import mu.KotlinLogging
import org.apache.tomcat.jni.Library
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class FileController {
    private val log = KotlinLogging.logger{}

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFile(@PathVariable id: Int): FileSystemResource {
        val file = DataManagerFactory.fileInfoDao.get(id)!!
        return FileSystemResource(file.path)
    }

    @PostMapping
    fun uploadFile(@RequestParam showId: String, @RequestParam season: Int, @RequestParam episode: Int, @RequestParam file: MultipartFile) {
        log.info { "[uploadFile] - showId: $showId, season: $season, episode: $episode, file: ${file.originalFilename}-${file.contentType}" }
        val show = LibraryManager.getOrCreateShow(showId)
        val episodeInfo = LibraryManager.getOrCreateEpisode(show, season, episode)
        val dotIdx = file.originalFilename!!.lastIndexOf('.')
        val extension = file.originalFilename!!.substring(dotIdx + 1)
        val path = LibraryManager.fileCrawler.importData(VideoFileInfo(
                name = show.name,
                season = episodeInfo.season.toString(),
                episode = episodeInfo.episodeNumber.toString(),
                episodeName = episodeInfo.name,
                path = ""
        ), extension, file.bytes)
        val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                id = null,
                codec = "",
                bitrate = "",
                resolution = "",
                duration = null,
                path = path
        ))
        episodeInfo.fileId = fileId
        DataManagerFactory.videoDao.update(episodeInfo)
    }

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
//            val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
//                    id = null,
//                    codec = "",
//                    bitrate = "",
//                    resolution = "",
//                    duration = null,
//                    path = path
//            ))
//            val videoLocally = DataManagerFactory.videoDao.findFromParent(show.imdbId, videoInfo.season.toInt(), videoInfo.episode.toInt())
//            if (videoLocally.isEmpty()) {
//                val tmdbEpisode = TMDbManager.apiAccess.tvEpisodes.getEpisode(show.externalIds.tmdb!!,
//                        videoInfo.season.toInt(), videoInfo.episode.toInt(), "en", TmdbTvEpisodes.EpisodeMethod.external_ids)
//                DataManagerFactory.videoDao.insert(Video(
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
//                DataManagerFactory.videoDao.update(local)
//            }
//
//            return "Successfully imported"
//        } catch (e: FileParseException) {
//            return e.message ?: "Error"
//        }
//    }

    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Int) {
        DataManagerFactory.fileInfoDao.delete(id)
    }
}