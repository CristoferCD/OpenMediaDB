package data.tmdb

import app.library.LibraryManager
import org.junit.Test

import org.junit.Assert.*

class TMDbManagerTest {

    @Test
    fun findByName() {
        TMDbManager.findByName("Game of Thrones")
    }
}