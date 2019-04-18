package app.controller

import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder

abstract class BaseController {

    protected  val log = KotlinLogging.logger {}

    fun getAuthenticatedUser(): Int? {
        val username = SecurityContextHolder.getContext().authentication.name
        val user = DataManagerFactory.userDao.findByName(username)
        return user?.id
    }
}