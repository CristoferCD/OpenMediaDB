package app.controller

import DataManagerFactory
import app.library.LibraryManager
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder

internal abstract class BaseController {

    protected val log = KotlinLogging.logger {}

    @Autowired
    protected lateinit var dataManagerFactory: DataManagerFactory
    @Autowired
    protected lateinit var libraryManager: LibraryManager

    fun getAuthenticatedUser(): Int? {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = dataManagerFactory.userDao.findByName(username)
        return user?.id
    }
}