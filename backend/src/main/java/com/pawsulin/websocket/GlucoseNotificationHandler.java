package com.pawsulin.websocket;

import com.pawsulin.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handles WebSocket lifecycle events for the {@code /ws/notifications} endpoint.
 * On connect, the session is registered with {@link NotificationService} using the
 * user ID that was injected during handshake by {@link com.pawsulin.security.JwtHandshakeInterceptor}.
 */
@Component
@Slf4j
public class GlucoseNotificationHandler extends TextWebSocketHandler {

    private static final String USER_ID_ATTRIBUTE = "userId";

    @Autowired
    private NotificationService notificationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(USER_ID_ATTRIBUTE);
        if (userId == null) {
            log.warn("WebSocket session {} has no userId attribute; closing", session.getId());
            try {
                session.close(CloseStatus.POLICY_VIOLATION);
            } catch (Exception e) {
                log.warn("Error closing unauthorized WebSocket session", e);
            }
            return;
        }
        notificationService.registerSession(userId, session);
        log.info("WebSocket connection established for user: {}, session: {}", userId, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(USER_ID_ATTRIBUTE);
        if (userId != null) {
            notificationService.removeSession(userId, session);
        }
        log.info("WebSocket connection closed for session: {}, status: {}", session.getId(), status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Clients only consume; ignore any incoming text
        log.debug("Received message from client session {}: ignored", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = (Long) session.getAttributes().get(USER_ID_ATTRIBUTE);
        log.error("WebSocket transport error for session: {}, user: {}", session.getId(), userId, exception);
        if (userId != null) {
            notificationService.removeSession(userId, session);
        }
    }
}
