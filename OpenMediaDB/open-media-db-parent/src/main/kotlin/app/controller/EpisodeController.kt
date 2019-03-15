package app.controller

import DataManagerFactory
import data.Video
import data.request.BooleanActionRB
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/episodes")
class EpisodeController {

    @GetMapping
    fun findEpisode(@RequestParam("show") showId: String,
                    @RequestParam(required = false) season: Int?,
                    @RequestParam(required = false) episode: Int?): List<Video> {
        return DataManagerFactory.videoDao.findFromParent(showId, season, episode)
    }

    @GetMapping("/{id}")
    fun getEpisode(@PathVariable id: String): Video {
        return DataManagerFactory.videoDao.get(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found")
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: String, @RequestBody booleanAction: BooleanActionRB): Boolean {
        val user = DataManagerFactory.userDao.findByName(SecurityContextHolder.getContext().authentication.name)
        DataManagerFactory.videoDao.markWatched(booleanAction.actionValue, user!!.id!!, id.toInt())
        return booleanAction.actionValue
    }
}