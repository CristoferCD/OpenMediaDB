package app.controller

import DataManagerFactory
import SubtitleManager
import data.Subtitle
import data.Video
import data.request.BooleanActionRB
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/episodes")
internal class EpisodeController : BaseController() {

    @GetMapping
    fun findEpisode(@RequestParam("show") showId: String,
                    @RequestParam(required = false) season: Int?,
                    @RequestParam(required = false) episode: Int?): List<Video> {
        val user = getAuthenticatedUser()
        return dataManagerFactory.videoDao.findFromParent(showId, season, episode, user)
    }

    @GetMapping("/{id}")
    fun getEpisode(@PathVariable id: Int): Video {
        val video = dataManagerFactory.videoDao.get(id)
        if (video != null)
            return video
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    @GetMapping("/{id}/subtitles")
    fun listAvailableSubtitles(@PathVariable id: Int): List<Subtitle> {
        val video = dataManagerFactory.videoDao.get(id)
        if (video == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
        } else {
            val show = dataManagerFactory.showDao.get(video.showId)
            return SubtitleManager.search(show!!.name, video.season, video.episodeNumber)
        }
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: Int, @RequestBody booleanAction: BooleanActionRB): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.videoDao.markWatched(booleanAction.actionValue, user, id)
        return booleanAction.actionValue
    }
}