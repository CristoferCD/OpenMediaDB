package app.controller

import data.Show
import data.response.PagedResponse
import data.tmdb.TMDbManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/shows")
internal class ShowController : BaseController() {

    @PostMapping
    fun registerShow(@RequestParam imdbId: String) {
        libraryManager.createShow(imdbId)
    }

    @GetMapping
    fun getList(): List<Show> {
        return dataManagerFactory.showDao.getAll()
    }

    @GetMapping("/following")
    fun getFollowing(): List<Show> {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        return dataManagerFactory.showDao.listFollowing(user)
    }

    @PostMapping("/{id}/follow")
    fun doFollow(@PathVariable id: String, @RequestParam value: Boolean): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.showDao.follow(value, id, user)
        //TODO: check
        return value
    }

    @GetMapping("/find")
    fun find(@RequestParam("q") query: String): PagedResponse<Show> {
        return PagedResponse(dataManagerFactory.showDao.find(query), 0, 0, 0)
    }

    @GetMapping("/{id}")
    fun getShow(@PathVariable id: String): Show {
        val show = dataManagerFactory.showDao.get(id)
        return show ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Show with requested id doesn't exist")
    }

    @DeleteMapping("/{id}")
    fun deleteShow(@PathVariable id: String, @RequestParam deleteFiles: Boolean): Boolean {
        TODO("Delete show and files if requested")
    }

    @GetMapping("/search")
    fun search(@RequestParam query: String, @RequestParam(required = false) page: Int?): PagedResponse<Show> {
        return if (page == null) TMDbManager.search(query) else TMDbManager.search(query, page)
    }
}