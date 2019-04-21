package app.controller

import DataManagerFactory
import data.Video
import data.request.BooleanActionRB
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/episodes")
class EpisodeController: BaseController() {

    @GetMapping
    fun findEpisode(@RequestParam("show") showId: String,
                    @RequestParam(required = false) season: Int?,
                    @RequestParam(required = false) episode: Int?): List<Video> {
        val user = getAuthenticatedUser()
        return DataManagerFactory.videoDao.findFromParent(showId, season, episode, user)
    }

    @GetMapping("/{id}")
    fun getEpisode(@PathVariable id: String): Video {
        val video = DataManagerFactory.videoDao.get(id)
        if (video != null)
            return video
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: String, @RequestBody booleanAction: BooleanActionRB): Boolean {
        val user = getAuthenticatedUser() ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        DataManagerFactory.videoDao.markWatched(booleanAction.actionValue, user, id.toInt())
        return booleanAction.actionValue
    }
}