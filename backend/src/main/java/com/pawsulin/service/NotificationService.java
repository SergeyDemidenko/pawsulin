package com.pawsulin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pawsulin.dto.NotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages WebSocket sessions per user and broadcasts notification messages.
 * Sessions are wrapped in {@link ConcurrentWebSocketSessionDecorator} to ensure
 * thread-safe message sending without synchronising on the session object itself.
 */
@Service
@Slf4j
public class NotificationService {

    /** Maximum time (ms) to wait for a send operation before giving up. */
    private static final int SEND_TIME_LIMIT_MS = 5_000;
    /** Maximum outbound buffer size per session (bytes). */
    private static final int BUFFER_SIZE_LIMIT = 64 * 1024;

    private final ConcurrentHashMap<Long, List<ConcurrentWebSocketSessionDecorator>> userSessions =
            new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public NotificationService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Registers a WebSocket session for the given user.
     *
     * @param userId  the authenticated user identifier
     * @param session the WebSocket session to register
     */
    public void registerSession(Long userId, WebSocketSession session) {
        ConcurrentWebSocketSessionDecorator safeSession =
                new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT_MS, BUFFER_SIZE_LIMIT);
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(safeSession);
        log.info("WebSocket session registered for user: {}, total sessions: {}", userId,
                userSessions.get(userId).size());
    }

    /**
     * Removes a WebSocket session for the given user.
     *
     * @param userId  the authenticated user identifier
     * @param session the original WebSocket session to remove
     */
    public void removeSession(Long userId, WebSocketSession session) {
        List<ConcurrentWebSocketSessionDecorator> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.removeIf(decorator -> decorator.getId().equals(session.getId()));
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.info("WebSocket session removed for user: {}", userId);
    }

    /**
     * Sends a notification message to all active WebSocket sessions for the given user.
     *
     * @param userId  the target user identifier
     * @param message the notification message to send
     */
    public void sendNotification(Long userId, NotificationMessage message) {
        List<ConcurrentWebSocketSessionDecorator> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            log.debug("No active WebSocket sessions for user: {}", userId);
            return;
        }

        try {
            String payload = objectMapper.writeValueAsString(message);
            TextMessage textMessage = new TextMessage(payload);
            for (ConcurrentWebSocketSessionDecorator session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        log.warn("Failed to send notification to session: {}", session.getId(), e);
                    }
                }
            }
            log.info("Notification sent to user: {}, type: {}", userId, message.getAlertType());
        } catch (IOException e) {
            log.error("Failed to serialize notification message", e);
        }
    }
}
