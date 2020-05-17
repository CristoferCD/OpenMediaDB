package app.controller.admin

import app.controller.AdminController
import app.controller.BaseController

internal class AdminControllerImpl : AdminController, BaseController() {
    override fun refreshLibrary() {
        log.info { "Requested to refresh library" }
        libraryManager.refreshLibrary()
    }
}