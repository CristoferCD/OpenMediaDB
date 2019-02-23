package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.ExternalIds
import data.FileInfo
import data.Video
import data.tmdb.TMDbManager
import exceptions.FileParseException
import info.movito.themoviedbapi.TmdbTvEpisodes
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class FileController {
    @GetMapping("/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getFile(@PathVariable id: Int): FileSystemResource {
        val file = DataManagerFactory.fileInfoDao.get(id)!!
        return FileSystemResource(file.path)
    }

    @PostMapping
    fun uploadFile(@RequestParam file: MultipartFile): String {
        println("Received ${file.originalFilename} and type ${file.contentType}")
        val dotIdx = file.originalFilename!!.lastIndexOf('.')
        val nameWithoutExtension = file.originalFilename!!.substring(0, dotIdx)
        val extension = file.originalFilename!!.substring(dotIdx + 1)
        try {
            val videoInfo = LibraryManager.fileCrawler.parseFileName(nameWithoutExtension)
            val show = LibraryManager.getOrCreateShow(videoInfo.name)
            val path = LibraryManager.fileCrawler.importData(videoInfo, extension, file.bytes)
            val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                    id = null,
                    codec = "",
                    bitrate = "",
                    resolution = "",
                    duration = null,
                    path = path
            ))
            val videoLocally = DataManagerFactory.videoDao.findFromParent(show.imdbId, videoInfo.season.toInt(), videoInfo.episode.toInt())
            if (videoLocally.isEmpty()) {
                val tmdbEpisode = TMDbManager.apiAccess.tvEpisodes.getEpisode(show.externalIds.tmdb!!,
                        videoInfo.season.toInt(), videoInfo.episode.toInt(), "en", TmdbTvEpisodes.EpisodeMethod.external_ids)
                DataManagerFactory.videoDao.insert(Video(
                        id = null,
                        showId = show.imdbId,
                        imdbId = tmdbEpisode.externalIds.imdbId,
                        sinopsis = tmdbEpisode.overview,
                        name = tmdbEpisode.name,
                        episodeNumber = tmdbEpisode.episodeNumber,
                        season = tmdbEpisode.seasonNumber,
                        imgPoster = null,
                        fileId = fileId,
                        externalIds = ExternalIds(imdb = tmdbEpisode.externalIds?.imdbId,
                                tvdb = tmdbEpisode.externalIds?.tvdbId?.toInt(),
                                tmdb = tmdbEpisode.id)
                ))
            } else {
                val local = videoLocally.first()
                local.fileId = fileId
                DataManagerFactory.videoDao.update(local)
            }

            return "Successfully imported"
        } catch (e: FileParseException) {
            return e.message ?: "Error"
        }
    }

    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: Int) {
        DataManagerFactory.fileInfoDao.delete(id)
    }
}