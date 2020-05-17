package app.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Administrative actions")
internal class AdminController : BaseController() {


    @PostMapping("/refreshLibrary")
    @Operation(summary = "Full library refresh", description = "Refresh information for all media in the library")
    fun refreshLibrary() {
        log.info { "Requested to refresh library" }
        libraryManager.refreshLibrary()
    }
}