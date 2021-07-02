package com.websocket.service;

import com.websocket.config.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebSocketService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    private static final String OUTGOING_CHANNEL_EVENT = "/queue/event";

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SessionManager sessionManager;


    public void sendEvent(Integer userId, String message) {
        List<String> sessionList = getSessionsByUserId(userId);
        for (String session : sessionList) {
            if (null == session || session.isEmpty()) {
                logger.warn("Session is null or empty for device account {}", userId);
                continue;
            }
            simpMessagingTemplate.convertAndSendToUser(session, OUTGOING_CHANNEL_EVENT, message, createHeaders(session));
        }
    }

    private List<String> getSessionsByUserId(Integer userId) {
        List<String> sessionList = new ArrayList<>();
        List<String> userSessions = sessionManager.getSessionsByUserId(userId);
        if (null != userSessions && !userSessions.isEmpty()) {
            logger.debug("Found WebSocket sessions for account {}, sessions: {}", userId, userSessions);
            sessionList.addAll(userSessions);
        } else {
            logger.debug("No WebSocket sessions for account {}", userId);
        }
        return sessionList;
    }

    private static MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
