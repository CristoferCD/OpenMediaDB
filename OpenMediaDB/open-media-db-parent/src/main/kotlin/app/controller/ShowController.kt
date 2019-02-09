package app.controller

import DataManagerFactory
import data.Show
import data.request.FollowRequestRB
import data.tmdb.TMDbBuilder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shows")
class ShowController {

    @GetMapping("/following")
    fun getFollowing() : List<Show> {
        val authenticatedUser = 1
        return DataManagerFactory.showDao.listFollowing(authenticatedUser)
    }

    @PostMapping("/following")
    fun doFollow(@RequestBody followRequest: FollowRequestRB) : Boolean {
        val authenticatedUser = 1
        DataManagerFactory.showDao.follow(followRequest.doFollow, followRequest.showId, authenticatedUser)
        //TODO: check
        return true
    }

    @GetMapping("/all")
    fun getAllLocal(): List<String> {
        val returnList = mutableListOf<String>()
        DataManagerFactory.showDao.getAll().forEach {
            returnList.add("${it.imdbId} - ${it.name}")
        }
        return returnList
    }

    @GetMapping("/search")
    fun searchOnline(@RequestParam name: String): List<String> {
        val returnList = mutableListOf<String>()
        val api = TMDbBuilder().create()
        api.search.searchTv(name, "en", 0).results?.let {
            it.forEach {
                returnList.add("${it.name} - ${it.overview} - ${it.userRating}")
            }
        }
        api.search.searchMovie(name, null, "en", true, 0).results?.let {
            it.forEach {
                returnList.add("${it.title} - ${it.overview} - ${it.userRating}")
            }
        }
        return returnList
    }
}