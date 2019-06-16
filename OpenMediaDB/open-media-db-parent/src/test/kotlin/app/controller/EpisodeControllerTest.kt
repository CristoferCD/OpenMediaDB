package app.controller

import data.User
import exceptions.ExistingEntityException
import io.mockk.every
import io.mockk.spyk
import org.junit.Test

import org.junit.Assert.*
import kotlin.test.assert

class EpisodeControllerTest {

    @Test
    fun listAvailableSubtitles() {
        ShowController().registerShow("tt0944947")
        val episode = DataManagerFactory.videoDao.findFromParent("tt0944947", 8, 6)
        val subs = EpisodeController().listAvailableSubtitles(episode.first().id!!)
        println(subs)
    }

    @Test
    fun findEpisode() {
        val controller = spyk(EpisodeController())
        try {
            DataManagerFactory.userDao.insert(User(null, "test", "test"))
        } catch(e: ExistingEntityException) {}
        val userid = DataManagerFactory.userDao.getAll().first().id
        every {
            controller.getAuthenticatedUser()
        } returns userid

        val video = DataManagerFactory.videoDao.getAll().first()
        DataManagerFactory.videoDao.markWatched(true, video.id!!, userid!!)

        val singleEpisode = controller.getEpisode(video.id!!)
        assert (singleEpisode.seen == true)

        val episodes = controller.findEpisode(video.showId, null, null)
        assert (episodes.find { it.id == video.id }?.seen == true)
    }
}