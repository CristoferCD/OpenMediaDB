package app.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import data.request.AuthTokenRB
import data.request.UserRB
import mu.KotlinLogging
import org.apache.catalina.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(val authManager: AuthenticationManager): UsernamePasswordAuthenticationFilter() {

    private val log = KotlinLogging.logger {}

    //TODO: move to properties file
    private val tokenDuration = 30
    private val tokenPrefix = "Bearer "
    private val secret = "Secret"
    private val header = "Authorization"

    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        try {
            val userCredentials = jacksonObjectMapper().readValue<UserRB>(request?.inputStream, UserRB::class.java)
            return authManager.authenticate(UsernamePasswordAuthenticationToken(userCredentials.name, userCredentials.password))
        } catch (e: Exception) {
            log.error(e) {"Error trying to authenticate request"}
        }
        throw BadCredentialsException("Failed to authenticate request")
    }

    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {
        val token = JWT.create()
                .withSubject((authResult?.principal as UserDetails).username)
                .withExpiresAt(Date.from(LocalDateTime.now().plusDays(tokenDuration.toLong())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC512(secret.toByteArray()))
        response?.addHeader(header, "$tokenPrefix$token")
        val tokenResponse = jacksonObjectMapper().writeValueAsString(AuthTokenRB(token))
        response?.outputStream.use {
            it?.write(tokenResponse.toByteArray())
        }
    }
}