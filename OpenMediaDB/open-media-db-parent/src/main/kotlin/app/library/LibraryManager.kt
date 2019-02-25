package app.library

import DataManagerFactory
import FileCrawler
import data.Show
import data.tmdb.TMDbManager
import exceptions.ExistingEntityException

internal object LibraryManager {
    val fileCrawler by lazy { FileCrawler() }

    fun getOrCreateShow(name: String): Show {
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

    private fun createShowEntry(show: Show) {
        show.path = fileCrawler.libraryRoot + "\\" + show.name
        DataManagerFactory.showDao.insert(show)
    }
}

enum class LibraryItemType {
    TVSHOW,
    MOVIE,
    MUSIC
}