package data.tmdb

import io.kotlintest.specs.AbstractAnnotationSpec

class TMDbManagerTest {

    @AbstractAnnotationSpec.Test
    fun findByName() {
        TMDbManager.findByName("Game of Thrones")
    }

    @AbstractAnnotationSpec.Test
    fun getEpisode() {
        val episode = TMDbManager.getEpisode(1399, 8, 1)
        println(episode)
    }
}