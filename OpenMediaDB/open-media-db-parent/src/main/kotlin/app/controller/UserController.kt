package app.controller

import data.User
import data.request.UserRB
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Users")
internal interface UserController {

    @PostMapping("/signup")
    fun signup(@RequestBody user: UserRB)
}