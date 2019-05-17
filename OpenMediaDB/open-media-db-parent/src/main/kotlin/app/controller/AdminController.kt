package app.controller

import app.library.LibraryManager
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController: BaseController() {

    @PostMapping("/refreshLibrary")
    fun refreshLibrary() {
        log.info { "Requested to refresh library" }
        LibraryManager.refreshLibrary()
    }
}