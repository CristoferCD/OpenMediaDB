package app.controller

import data.User
import data.request.UserRB
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @PostMapping("/signup")
    fun signup(@RequestBody user: UserRB) {
        DataManagerFactory.userDao.insert(User(
                id = null,
                name = user.name,
                password = passwordEncoder.encode(user.password)
        ))
        //TODO: Auto log in
    }
}