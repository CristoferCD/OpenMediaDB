package app.controller

import DataManagerFactory
import app.library.LibraryManager
import data.Show
import data.request.BooleanActionRB
import data.request.SearchRB
import data.tmdb.TMDbBuilder
import data.tmdb.TMDbManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shows")
class ShowController {

    @PostMapping
    fun registerShow(@RequestParam imdbId: String): String {
        println("Requested creation of $imdbId")
        LibraryManager.getOrCreateShow(imdbId)
        return "TODO"
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

    @GetMapping("/find")
    fun find(@RequestParam("q") query: String): List<Show> {
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

    @GetMapping("/search")
    fun search(@RequestParam query: String, @RequestParam(required = false) page: Int?): SearchRB {
        return if (page == null) TMDbManager.search(query) else TMDbManager.search(query, page)
    }
}