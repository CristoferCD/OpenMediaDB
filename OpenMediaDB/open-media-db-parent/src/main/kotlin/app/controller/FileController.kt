package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.FileInfo
import data.Show
import data.Video
import data.tmdb.TMDbManager
import exceptions.FileParseException
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.TmdbTvEpisodes
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/files")
class FileController {
    @GetMapping("/{id}")
    fun getFile(@PathVariable id: Int) {
        TODO()
    }

    @PostMapping
    fun uploadFile(@RequestParam file: MultipartFile): String {
        println("Received ${file.originalFilename} and type ${file.contentType}")
        val dotIdx = file.originalFilename!!.lastIndexOf('.')
        val nameWithoutExtension = file.originalFilename!!.substring(0, dotIdx)
        val extension = file.originalFilename!!.substring(dotIdx + 1)
        try {
            val videoInfo = LibraryManager.fileCrawler.parseFileName(nameWithoutExtension)
            val showLocally = DataManagerFactory.showDao.find(videoInfo.name)
            var showId = ""
            var tmdbShowId = 0
            if (showLocally.isEmpty()) {
                //TODO: build show path and build images path from tmdb
                val foundShow = TMDbManager.apiAccess.search.searchTv(videoInfo.name, "en", 0).results?.firstOrNull()
                        ?: TODO()
                tmdbShowId = foundShow.id
                val tmdbShow = TMDbManager.apiAccess.tvSeries.getSeries(foundShow.id, "en", TmdbTV.TvMethod.external_ids)
                val showPath = LibraryManager.fileCrawler.libraryRoot + "\\" + tmdbShow.name
                showId = DataManagerFactory.showDao.insert(Show(
                        imdbId = tmdbShow.externalIds.imdbId,
                        name = tmdbShow.originalName ?: tmdbShow.name,
                        sinopsis = tmdbShow.overview,
                        imgPoster = tmdbShow.posterPath,
                        imgBackground = tmdbShow.backdropPath,
                        path = showPath
                ))
            }
            //TODO; extension
            val path = LibraryManager.fileCrawler.importData(videoInfo, extension, file.bytes)
            val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                    id = null,
                    codec = "",
                    bitrate = "",
                    resolution = "",
                    duration = null,
                    path = path
            ))
            val videoLocally = DataManagerFactory.videoDao.findFromParent(showId, videoInfo.season.toInt(), videoInfo.episode.toInt())
            if (videoLocally.isEmpty()) {
                val tmdbEpisode = TMDbManager.apiAccess.tvEpisodes.getEpisode(tmdbShowId,
                        videoInfo.season.toInt(), videoInfo.episode.toInt(), "en", TmdbTvEpisodes.EpisodeMethod.external_ids)
                DataManagerFactory.videoDao.insert(Video(
                        id = null,
                        showId = showId,
                        imdbId = tmdbEpisode.externalIds.imdbId,
                        sinopsis = tmdbEpisode.overview,
                        name = tmdbEpisode.name,
                        episodeNumber = tmdbEpisode.episodeNumber,
                        season = tmdbEpisode.seasonNumber,
                        imgPoster = null,
                        fileId = fileId
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
        TODO()
    }
}