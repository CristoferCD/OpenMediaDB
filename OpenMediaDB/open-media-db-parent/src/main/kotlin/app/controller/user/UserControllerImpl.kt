package app.controller.user

import app.controller.BaseController
import app.controller.UserController
import data.User
import data.request.UserRB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

internal class UserControllerImpl : UserController, BaseController() {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun signup(user: UserRB) {
        dataManagerFactory.userDao.insert(User(
                id = null,
                name = user.name,
                password = passwordEncoder.encode(user.password)
        ))
        //TODO: Auto log in
    }
}