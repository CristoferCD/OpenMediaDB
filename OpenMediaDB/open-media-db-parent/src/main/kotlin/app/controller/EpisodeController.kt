package app.controller

import SubtitleManager
import data.Subtitle
import data.Video
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import providers.SubtitleProviderId

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
        return video ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
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

    @GetMapping("/subtitle", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getSubtitle(@RequestParam id: SubtitleProviderId, @RequestParam url: String): ByteArrayResource {
        val bytes = SubtitleManager.get(Subtitle(id, "", "", url))
        return ByteArrayResource(bytes!!)
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: Int, @RequestParam value: Boolean): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.videoDao.markWatched(value, user, id)
        return value
    }
}