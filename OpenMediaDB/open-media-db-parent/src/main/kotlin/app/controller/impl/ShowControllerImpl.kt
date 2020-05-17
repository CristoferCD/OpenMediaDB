package app.controller.impl

import app.controller.BaseController
import app.controller.ShowController
import data.Show
import data.response.PagedResponse
import data.tmdb.TMDbManager
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
internal class ShowControllerImpl : ShowController, BaseController() {
    override fun registerShow(imdbId: String) {
        libraryManager.createShow(imdbId)
    }

    override fun getList(): List<Show> {
        return dataManagerFactory.showDao.getAll()
    }

    override fun getFollowing(): List<Show> {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        return dataManagerFactory.showDao.listFollowing(user)
    }

    override fun doFollow(id: String, value: Boolean): Boolean {
        val user = getAuthenticatedUser()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in")
        dataManagerFactory.showDao.follow(value, id, user)
        //TODO: check
        return value
    }

    override fun find(query: String): PagedResponse<Show> {
        return PagedResponse(dataManagerFactory.showDao.find(query), 0, 0, 0)
    }

    override fun getShow(id: String): Show {
        val show = dataManagerFactory.showDao.get(id)
        return show ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Show with requested id doesn't exist")
    }

    override fun deleteShow(id: String, deleteFiles: Boolean): Boolean {
        TODO("Delete show and files if requested")
    }

    override fun search(query: String, page: Int?): PagedResponse<Show> {
        return if (page == null) TMDbManager.search(query) else TMDbManager.search(query, page)
    }
}