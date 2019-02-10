package app.controller

import DataManagerFactory
import data.Video
import data.request.BooleanActionRB
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/episodes")
class EpisodeController {

    @GetMapping
    fun findEpisode(@RequestParam("show") showId: String, @RequestParam season: Int, @RequestParam(required = false) episode: Int?) {
        println("Requested $showId $season x ${episode ?: ""}")
    }

    @GetMapping("/{id}")
    fun getEpisode(@PathVariable id: String): Video {
        return DataManagerFactory.videoDao.get(id)!!
    }

    @PostMapping("/{id}/seen")
    fun markSeen(@PathVariable id: String, @RequestBody booleanAction: BooleanActionRB): Boolean {
        TODO()
    }
}