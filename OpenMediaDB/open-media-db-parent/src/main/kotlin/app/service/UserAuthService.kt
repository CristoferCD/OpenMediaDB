package app.service

import DataManagerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserAuthService : UserDetailsService {

    @Autowired
    private lateinit var dataManagerFactory: DataManagerFactory

    override fun loadUserByUsername(username: String?): UserDetails? {
        val user = dataManagerFactory.userDao.findByName(username!!)
                ?: throw UsernameNotFoundException("Username $username not found")
        return User(user.name, user.password, emptyList())
    }
}