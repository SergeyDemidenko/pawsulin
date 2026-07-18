package com.pawsulin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

/**
 * Validates a JWT token supplied as the {@code token} query parameter during
 * the WebSocket handshake and stores the resolved user ID in the session
 * attributes so the {@link com.pawsulin.websocket.GlucoseNotificationHandler} can retrieve it.
 */
@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final String USER_ID_ATTRIBUTE = "userId";

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        String query = uri.getQuery();
        if (!StringUtils.hasText(query)) {
            log.warn("WebSocket handshake rejected: missing token query parameter");
            return false;
        }

        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring("token=".length());
                break;
            }
        }

        if (!StringUtils.hasText(token) || !tokenProvider.validateToken(token)) {
            log.warn("WebSocket handshake rejected: invalid or missing JWT token");
            return false;
        }

        try {
            Long userId = tokenProvider.getUserIdFromToken(token);
            attributes.put(USER_ID_ATTRIBUTE, userId);
            log.info("WebSocket handshake authorized for user: {}", userId);
            return true;
        } catch (Exception e) {
            log.warn("WebSocket handshake rejected: could not extract user ID from token", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
    }
}
