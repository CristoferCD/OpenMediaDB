package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.Show
import data.request.BooleanActionRB
import data.tmdb.TMDbBuilder
import data.tmdb.TMDbManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shows")
class ShowController {

    @PostMapping
    fun registerShow(@RequestParam imdbId: String): String {
        val show = TMDbManager.find(imdbId)
        println("Requested creation of $show")
        return if (show != null) {
            LibraryManager.createOrUpdateShow(show)
            "Successfully created show [${show.imdbId}] - ${show.name}"
        } else {
            "Unable to find show with id $imdbId"
        }
    }

    @GetMapping
    fun getList(): List<Show> {
        return DataManagerFactory.showDao.getAll()
    }

    @GetMapping("/following")
    fun getFollowing(): List<Show> {
        val user = DataManagerFactory.userDao.findByName(SecurityContextHolder.getContext().authentication.name)
        return DataManagerFactory.showDao.listFollowing(user?.id!!)
    }

    @PostMapping("/following")
    fun doFollow(@RequestBody booleanAction: BooleanActionRB): Boolean {
        val authenticatedUser = 1
        DataManagerFactory.showDao.follow(booleanAction.actionValue, booleanAction.showId, authenticatedUser)
        //TODO: check
        return true
    }

    @GetMapping("/search")
    fun search(@RequestParam("q") query: String): List<Show> {
        TODO("Implement fuzzy string search to all entries on db")
    }

    @GetMapping("/{id}")
    fun getShow(@PathVariable id: String): Show {
        return DataManagerFactory.showDao.get(id)!!
    }

    @DeleteMapping("/{id}")
    fun deleteShow(@PathVariable id: String, @RequestParam deleteFiles: Boolean): Boolean {
        TODO("Delete show and files if requested")
    }

    @GetMapping("/searchOnline")
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