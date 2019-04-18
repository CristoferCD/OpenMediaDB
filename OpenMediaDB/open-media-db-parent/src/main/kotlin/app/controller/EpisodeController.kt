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
                    @RequestParam(required = false) episode: Int?): ResponseEntity<List<Video>> {
        val user = getAuthenticatedUser()
        return ResponseEntity.ok(DataManagerFactory.videoDao.findFromParent(showId, season, episode, user))
    }

    @GetMapping("/{id}")
    fun getEpisode(@PathVariable id: String): ResponseEntity<Video> {
        val video = DataManagerFactory.videoDao.get(id)
        if (video != null)
            return ResponseEntity.ok(video)
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: String, @RequestBody booleanAction: BooleanActionRB): ResponseEntity<Boolean> {
        val user = getAuthenticatedUser() ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        DataManagerFactory.videoDao.markWatched(booleanAction.actionValue, user, id.toInt())
        return ResponseEntity.ok(booleanAction.actionValue)
    }
}