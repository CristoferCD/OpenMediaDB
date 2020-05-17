package app.controller.impl

import app.controller.BaseController
import app.controller.MovieController
import org.springframework.web.bind.annotation.RestController

@RestController
internal class MovieControllerImpl : MovieController, BaseController() {
    override fun getMovie(id: String) {
        TODO("Not yet implemented")
    }
}