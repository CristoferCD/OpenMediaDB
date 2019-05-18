package app.library

import DataManagerFactory
import FileCrawler
import data.FileInfo
import data.Show
import data.Video
import data.tmdb.TMDbManager
import exceptions.ExistingEntityException
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import java.io.File
import java.nio.file.Path

internal object LibraryManager {
    val fileCrawler by lazy { FileCrawler() }
    private val log = KotlinLogging.logger {}

    fun getOrCreateShow(imdbId: String): Show {
        return DataManagerFactory.showDao.get(imdbId)
                ?: run {
                    val show = TMDbManager.find(imdbId) ?: TODO("Throw show not found exception")
                    createShowEntry(show)
                    return show
                }
    }

    fun getOrCreateShowByName(name: String): Show {
        return DataManagerFactory.showDao.find(name).firstOrNull()
                ?: run {
                    val show = TMDbManager.findByName(name) ?: TODO("Throw show not found exception")
                    createShowEntry(show)
                    return show
                }
    }

    fun createOrUpdateShow(show: Show) {
        show.path = fileCrawler.libraryRoot + "\\" + show.name
        try {
            DataManagerFactory.showDao.insert(show)
        } catch (e: ExistingEntityException) {
            DataManagerFactory.showDao.update(show)
        }
    }

    fun registerAllEpisodes(showId: String) {
        val show = getOrCreateShow(showId)
        log.info { "Updating episodes for ${show.name}" }
        TMDbManager.getEpisodesFromSeason(show, 1..show.totalSeasons).forEach {
            log.info { "Registering episode $it" }
            val existingEpisode = DataManagerFactory.videoDao.findFromParent(showId, it.season, it.episodeNumber)
            if (existingEpisode.size == 1) {
                DataManagerFactory.videoDao.update(it.copy(id = existingEpisode.first().id, fileId = existingEpisode.first().fileId))
            } else {
                createEpisodeEntry(it)
            }
        }
    }

    fun getOrCreateEpisode(parent: Show, season: Int, episodeNumber: Int): Video {
        val username = SecurityContextHolder.getContext().authentication?.name
        val user = if (username != null) DataManagerFactory.userDao.findByName(username) else null
        return DataManagerFactory.videoDao.findFromParent(parent.imdbId, season, episodeNumber, user?.id).firstOrNull()
                ?: run {
                    val episode = TMDbManager.getEpisode(parent.externalIds.tmdb!!, season, episodeNumber)
                            ?: TODO("Throw episode not found exception")
                    return createEpisodeEntry(episode)
                }
    }

    fun refreshLibrary() {
        log.info { "Started library refresh" }
        val importResult = fileCrawler.importLibrary(File(fileCrawler.libraryRoot))
        log.info { "Imported library from ${fileCrawler.libraryRoot}: $importResult" }
        if (!importResult.failedImports.isEmpty()) throw Exception("Failed to import some items") //TODO: make custom exception

        val createdShows = mutableMapOf<String, Show>()
        importResult.successfulImports.forEach {
            if (!createdShows.containsKey(it.name)) {
                val show = getOrCreateShowByName(it.name)
                createdShows[it.name] = show
            }

            val episode = getOrCreateEpisode(createdShows[it.name]!!, it.season.toInt(), it.episode.toInt())
            if (episode.fileId != null) {
                val existingFile = DataManagerFactory.fileInfoDao.get(episode.fileId!!)
                if (existingFile != null) {
                    log.info { "Updating episode $episode" }
                    DataManagerFactory.fileInfoDao.update(existingFile.copy(path = it.path!!))
                } else {
                    log.info { "Registering file ${it.path} for episode $episode" }
                    insertFile(episode, it.path!!)
                }
            } else {
                log.info { "Registering file ${it.path} for episode $episode" }
                insertFile(episode, it.path!!)
            }
        }

        DataManagerFactory.showDao.getAll().forEach {
            registerAllEpisodes(it.imdbId)
        }
    }

    fun insertFile(episode: Video, path: Path): Int {
        //TODO: Load file info using ffmpeg
        val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                id = null,
                codec = "",
                bitrate = "",
                resolution = "",
                duration = null,
                path = path
        ))
        episode.fileId = fileId
        DataManagerFactory.videoDao.update(episode)
        return fileId
    }

    private fun createShowEntry(show: Show) {
        show.path = fileCrawler.libraryRoot + "\\" + show.name
        DataManagerFactory.showDao.insert(show)
        registerAllEpisodes(show.imdbId)
    }

    private fun createEpisodeEntry(video: Video): Video {
        val id = DataManagerFactory.videoDao.insert(video)
        return video.copy(id = id)
    }
}

enum class LibraryItemType {
    TVSHOW,
    MOVIE,
    MUSIC
}