package data.tmdb

import app.library.LibraryManager
import org.junit.Test

import org.junit.Assert.*

class TMDbManagerTest {

    @Test
    fun findByName() {
        TMDbManager.findByName("Game of Thrones")
    }

    @Test
    fun getEpisode() {
        val episode = TMDbManager.getEpisode(1399, 8, 1)
        println(episode)
    }
}