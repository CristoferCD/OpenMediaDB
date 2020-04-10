package data.tmdb

import io.kotest.core.spec.style.StringSpec


class TMDbManagerTest : StringSpec({
    "findByName" {
        TMDbManager.findByName("Game of Thrones")
    }

    "getEpisode" {
        val episode = TMDbManager.getEpisode(1399, 8, 1)
        println(episode)
    }
})