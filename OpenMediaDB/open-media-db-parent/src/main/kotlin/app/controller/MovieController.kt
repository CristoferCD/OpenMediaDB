package app.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/movies")
class MovieController {
    @GetMapping("/{id}")
    fun getMovie(@PathVariable id: String) {
        TODO()
    }
}