package com.lumbre.security.jwt

import com.lumbre.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService(private val jwtProperties: JwtProperties) {

    private fun getSignInKey(): SecretKey {
        // Converts the configured string secret into a SecretKey object
        return Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateToken(id: UUID?, username: String, email: String): String {
        requireNotNull(id) { "Id cannot be null" }

        return Jwts.builder()
            .subject(id.toString())
            .claim("username", username) // Adds a custom claim with the user's UUID\
            .claim("email", email)
            .issuedAt(Date()) // Sets the token issuance time to current time
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration)) // Sets expiration time
            .signWith(getSignInKey(), Jwts.SIG.HS256) // Signs the token with HMAC SHA-256
            .compact() // Builds and returns the compact JWT string
    }

    fun <T> extractClaim(token: String, claimName: String, clazz: Class<T>): T {
        return parseToken(token)
            .get(claimName, clazz) // Retrieves the specific claim
    }

    /**
     * Validates the token by checking its expiration.
     *
     * @param token the JWT token.
     * @return true if the token is still valid.
     */
    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token)
    }

    /**
     * Checks if the token has expired based on its "exp" claim.
     *
     * @param token the JWT token.
     * @return true if the token is expired.
     */
    private fun isTokenExpired(token: String): Boolean {
        val expiration = parseToken(token).expiration// Extracts expiration date from token

        return expiration.before(Date()) // Compares expiration with current time
    }

    private fun parseToken(token:String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }
}