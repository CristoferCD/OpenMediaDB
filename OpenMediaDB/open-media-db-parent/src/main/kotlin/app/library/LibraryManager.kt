package app.library

import DataManagerFactory
import FileCrawler
import data.Show
import data.VideoFileInfo
import data.tmdb.TMDbManager

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