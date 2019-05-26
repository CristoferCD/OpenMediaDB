package app.controller

import org.junit.Test

import org.junit.Assert.*

class EpisodeControllerTest {

    @Test
    fun listAvailableSubtitles() {
        ShowController().registerShow("tt0944947")
        val episode = DataManagerFactory.videoDao.findFromParent("tt0944947", 8, 6)
        val subs = EpisodeController().listAvailableSubtitles(episode.first().id!!)
        println(subs)
    }
}