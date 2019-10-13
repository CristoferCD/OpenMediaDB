package app.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import data.response.TokenExpiredResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

    //TODO: same properties as the other filter
    private val tokenPrefix = "Bearer "
    private val secret = "Secret"
    private val header = "Authorization"

    override fun doFilterInternal(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?) {
        try {
            if (request?.getHeader(header)?.startsWith(tokenPrefix) == true) {
                val authentication = getAuthentication(request)
                SecurityContextHolder.getContext().authentication = authentication
                chain?.doFilter(request, response)
            } else {
                chain?.doFilter(request, response)
                return
            }
        } catch (ex: TokenExpiredException) {
            val error = TokenExpiredResponse("Token expired", ex.message ?: "")
            response?.status = 403
            response?.contentType = "application/json"
            response?.outputStream.use {
                it?.write(error.json())
            }
        }
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(header)
        if (token != null) {
            val username = JWT.require(Algorithm.HMAC512(secret.toByteArray()))
                    .build().verify(token.replace(tokenPrefix, ""))
                    .subject

            return if (username != null) UsernamePasswordAuthenticationToken(username, null, emptyList()) else null
        }
        return null
    }
}