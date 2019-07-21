package app.controller

import DataManagerFactory
import data.User
import exceptions.ExistingEntityException
import io.mockk.every
import io.mockk.spyk
import org.junit.Test

class EpisodeControllerTest {

    private val dataManagerFactory = DataManagerFactory()

    @Test
    fun listAvailableSubtitles() {
        ShowController().registerShow("tt0944947")
        val episode = dataManagerFactory.videoDao.findFromParent("tt0944947", 8, 6)
        val subs = EpisodeController().listAvailableSubtitles(episode.first().id!!)
        println(subs)
    }

    @Test
    fun findEpisode() {
        val controller = spyk(EpisodeController())
        try {
            dataManagerFactory.userDao.insert(User(null, "test", "test"))
        } catch (e: ExistingEntityException) {
        }
        val userid = dataManagerFactory.userDao.getAll().first().id
        every {
            controller.getAuthenticatedUser()
        } returns userid

        val video = dataManagerFactory.videoDao.getAll().first()
        dataManagerFactory.videoDao.markWatched(true, video.id!!, userid!!)

        val singleEpisode = controller.getEpisode(video.id!!)
        assert(singleEpisode.seen == true)

        val episodes = controller.findEpisode(video.showId, null, null)
        assert(episodes.find { it.id == video.id }?.seen == true)
    }
}