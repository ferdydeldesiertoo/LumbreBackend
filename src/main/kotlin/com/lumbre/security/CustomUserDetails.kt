package com.lumbre.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class CustomUserDetails(
    private val id: UUID,
    private val username: String,
    private val email: String,
    private val password: String
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return emptyList()
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    fun getId(): UUID = id
    fun getEmail(): String = email
}
