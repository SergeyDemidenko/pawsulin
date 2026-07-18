package com.pawsulin.config;

import com.pawsulin.security.JwtHandshakeInterceptor;
import com.pawsulin.websocket.GlucoseNotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configures the WebSocket endpoint used for real-time glucose alert notifications.
 * Clients connect to {@code /ws/notifications?token=<jwt>}.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GlucoseNotificationHandler glucoseNotificationHandler;

    @Autowired
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        registry.addHandler(glucoseNotificationHandler, "/ws/notifications")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins(origins);
    }
}
