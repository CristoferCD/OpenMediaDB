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
        } catch(e: ExistingEntityException) {
            DataManagerFactory.showDao.update(show)
        }
    }

    fun registerAllEpisodes(showId: String) {
        val show = getOrCreateShow(showId)
        TMDbManager.getEpisodesFromSeason(show, 1..show.totalSeasons).forEach {
            createEpisodeEntry(it)
        }
    }

    fun getOrCreateEpisode(parent: Show, season: Int, episodeNumber: Int): Video {
        val username = SecurityContextHolder.getContext().authentication?.name
        val user = if (username != null) DataManagerFactory.userDao.findByName(username) else null
        return DataManagerFactory.videoDao.findFromParent(parent.imdbId, season, episodeNumber, user?.id).firstOrNull()
                ?: run {
                    val episode = TMDbManager.getEpisode(parent.externalIds.tmdb!!, season, episodeNumber) ?: TODO("Throw episode not found exception")
                    return createEpisodeEntry(episode)
                }
    }

    fun refreshLibrary() {
        val importResult = fileCrawler.importLibrary(File(fileCrawler.libraryRoot))
        log.debug { "Imported library from ${fileCrawler.libraryRoot}: $importResult" }
        if (!importResult.failedImports.isEmpty()) throw Exception("Failed to import some items") //TODO: make custom exception

        val createdShows = mutableMapOf<String, Show>()
        importResult.successfulImports.forEach {
            if (!createdShows.containsKey(it.name)) {
                val show = getOrCreateShowByName(it.name)
                createdShows[it.name] = show
            }

            val episode = getOrCreateEpisode(createdShows[it.name]!!, it.season.toInt(), it.episode.toInt())
            val fileId = DataManagerFactory.fileInfoDao.insert(FileInfo(
                    id = null,
                    codec = "",
                    bitrate = "",
                    resolution = "",
                    duration = null,
                    path = it.path!!
            ))
            episode.fileId = fileId
            DataManagerFactory.videoDao.update(episode)
        }
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