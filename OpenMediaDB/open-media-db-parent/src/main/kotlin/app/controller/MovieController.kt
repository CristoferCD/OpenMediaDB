package app.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movies")
@Tag(name = "Movies", description = "Movie related operations")
class MovieController {
    @GetMapping("/{id}")
    fun getMovie(@PathVariable id: String) {
        TODO()
    }
}