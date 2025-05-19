package com.lumbre.security.jwt

import com.lumbre.security.CustomAuthenticationEntryPoint
import com.lumbre.security.CustomUserDetailsService
import com.lumbre.security.exception.JwtExpiredException
import com.lumbre.security.exception.JwtInvalidException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) :
    OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        try {
            val authHeader = request.getHeader("Authorization")

            // Checks if the Authorization header is missing or doesn't start with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response)
                return
            }

            // Extracts the token from the header (removes "Bearer ")
            val jwt = authHeader.substring(7)

            // Extracts the userId claim from the token
            val id = UUID.fromString(jwtService.extractClaim(jwt, "sub", String::class.java))

            // Validates token and ensures no previous authentication exists
            if (jwtService.isTokenValid(jwt) && SecurityContextHolder.getContext().authentication == null) {

                // Loads the UserDetails by user ID
                val userDetails = customUserDetailsService.loadUserByUserId(id)

                // Creates an authentication token with user details and authorities
                val authToken =
                    UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

                // Attaches request-specific details to the auth token (e.g., IP, session)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                // Sets the authentication into the security context
                SecurityContextHolder.getContext().authentication = authToken
            }

            log.info("User authenticated: $id")

            // Continues the filter chain
            filterChain.doFilter(request, response)

        } catch (e: ExpiredJwtException) {
            SecurityContextHolder.clearContext()
            log.warn("JWT expired for user: ${e.claims?.subject}")

            // Token has expired, delegate to custom entry point with specific exception
            customAuthenticationEntryPoint.commence(request, response, JwtExpiredException("JWT is expired", e))
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
            log.warn("Invalid JWT. Reason: ${e.javaClass.name} - ${e.message}")

            when (e) {
                is MalformedJwtException, is SignatureException, is IllegalArgumentException -> {
                    // Token is invalid, malformed, or has wrong signature
                    customAuthenticationEntryPoint.commence(request, response, JwtInvalidException("Invalid JWT", e))
                }

                else -> {
                    // Any other unexpected error during token processing
                    customAuthenticationEntryPoint.commence(
                        request,
                        response,
                        JwtInvalidException("An error occurred while processing JWT", e)
                    )
                }
            }
        }
    }
}