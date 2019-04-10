package app.library

import DataManagerFactory
import FileCrawler
import data.Show
import data.Video
import data.tmdb.TMDbManager
import exceptions.ExistingEntityException
import org.springframework.security.core.context.SecurityContextHolder

internal object LibraryManager {
    val fileCrawler by lazy { FileCrawler() }

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
        val user = DataManagerFactory.userDao.findByName(SecurityContextHolder.getContext().authentication.name)
        return DataManagerFactory.videoDao.findFromParent(parent.imdbId, season, episodeNumber, user?.id).firstOrNull()
                ?: run {
                    val episode = TMDbManager.getEpisode(parent.externalIds.tmdb!!, season, episodeNumber) ?: TODO("Throw episode not found exception")
                    return createEpisodeEntry(episode)
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