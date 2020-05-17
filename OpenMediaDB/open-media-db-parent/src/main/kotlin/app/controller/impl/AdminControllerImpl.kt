package app.controller.impl

import app.controller.AdminController
import app.controller.BaseController
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdminControllerImpl : AdminController, BaseController() {
    override fun refreshLibrary() {
        log.info { "Requested to refresh library" }
        libraryManager.refreshLibrary()
    }
}