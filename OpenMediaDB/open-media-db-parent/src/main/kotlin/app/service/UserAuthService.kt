package app.service

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserAuthService : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails? {
        val user = DataManagerFactory.userDao.findByName(username!!)
        if (user == null) return user
        return User(user.name, user.password, emptyList())
    }
}