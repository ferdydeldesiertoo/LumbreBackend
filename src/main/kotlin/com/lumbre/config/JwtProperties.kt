package com.lumbre.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
class JwtProperties {
    lateinit var secret: String
    var expiration: Long = 0
}
