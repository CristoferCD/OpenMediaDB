package app.controller.episode

import SubtitleManager
import app.controller.BaseController
import app.controller.EpisodeController
import data.Subtitle
import data.SubtitleDownloadForm
import data.Video
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal class EpisodeControllerImpl : EpisodeController, BaseController() {
    override fun findEpisode(showId: String, season: Int?, episode: Int?): List<Video> {
        val user = getAuthenticatedUser()
        return dataManagerFactory.videoDao.findFromParent(showId, season, episode, user)
    }

    override fun getEpisode(id: Int): Video {
        val video = dataManagerFactory.videoDao.get(id)
        return video ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    override fun listAvailableSubtitles(id: Int): List<Subtitle> {
        val video = dataManagerFactory.videoDao.get(id)
        if (video == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
        } else {
            val show = dataManagerFactory.showDao.get(video.showId)
            return SubtitleManager.search(show!!.name, video.season, video.episodeNumber)
        }
    }

    override fun getSubtitle(form: SubtitleDownloadForm): ByteArrayResource {
        val bytes = SubtitleManager.get(form)
        return ByteArrayResource(bytes!!)
    }

    override fun markSeen(id: Int, value: Boolean): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.videoDao.markWatched(value, user, id)
        return value
    }
}