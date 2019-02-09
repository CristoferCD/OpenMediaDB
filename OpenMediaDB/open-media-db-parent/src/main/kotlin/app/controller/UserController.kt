package app.controller

import data.request.UserRB
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController {

    @PostMapping("/login")
    fun login(@RequestBody user: UserRB) {
        println("Requested login for user $user")
        //TODO: create session or expand existing. Return error if user doesn't exist
    }

    @PostMapping("/signup")
    fun signup(@RequestBody user: UserRB) {
        println("Requested signup for user $user")
        //TODO: Hash password and create new entry. Then log in
    }
}