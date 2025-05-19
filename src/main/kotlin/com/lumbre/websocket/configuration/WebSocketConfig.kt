package com.lumbre.websocket.configuration

import com.lumbre.security.CustomUserDetailsService
import com.lumbre.security.jwt.JwtService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.util.*

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Enable simple broker for topics and queues
        config.enableSimpleBroker("/topic", "/queue")
        // Prefix for messages annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app")
        // Prefix for user-specific messaging
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Register STOMP endpoint and allow SockJS fallback
        registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:5173")
            .setAllowedOriginPatterns("http://localhost:5173")
            .withSockJS()
            .setSessionCookieNeeded(true)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                // Wrap the message to access STOMP headers
                val accessor = StompHeaderAccessor.wrap(message)

                println("Incoming STOMP message: command=${accessor.command}")

                // Intercept CONNECT commands to authenticate WebSocket connections
                if (StompCommand.CONNECT == accessor.command) {
                    // Retrieve Authorization header (Bearer token)
                    val token = accessor.getFirstNativeHeader("Authorization")

                    // If no token or wrong format, skip auth
                    if (token == null || !token.startsWith("Bearer ")) {
                        return message
                    }

                    val jwt = token.substring(7)

                    // Validate token before setting user
                    if (jwtService.isTokenValid(jwt)) {
                        val id = UUID.fromString(
                            jwtService.extractClaim(jwt, "sub", String::class.java)
                        )

                        val userDetails = customUserDetailsService.loadUserByUserId(id)

                        // Create spring authentication
                        val auth = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        // Set authenticated user for this session
                        accessor.user = auth
                        SecurityContextHolder.getContext().authentication = auth
                        accessor.sessionAttributes?.put("SPRING.SESSION.PRINCIPAL", auth)

                        // Rebuild message with new headers
                        return MessageBuilder.createMessage(message.payload, accessor.messageHeaders)
                    }
                }
                // For other commands, forward message unchanged
                return message
            }
        })
    }
}
