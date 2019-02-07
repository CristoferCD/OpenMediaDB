package app.controller

import DataManagerFactory
import data.tmdb.TMDbBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shows")
class ShowController {

    @GetMapping("/all")
    fun getAllLocal(): List<String> {
        val returnList = mutableListOf<String>()
        DataManagerFactory.showDao.getAll().forEach {
            returnList.add("${it.imdbId} - ${it.name}")
        }
        return returnList
    }

    @GetMapping("/search")
    fun searchOnline(@RequestParam("name") name: String): List<String> {
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