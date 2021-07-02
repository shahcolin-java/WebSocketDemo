package com.websocket.controller;

import com.websocket.StatusEnum;
import com.websocket.config.SessionManager;
import com.websocket.dto.WebSocketRequest;
import com.websocket.model.WebSocketConnections;
import com.websocket.service.WebSocketConnectionService;
import com.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private static final String INCOMING_CHANNEL_REGISTER = "/register";
    private static final String OUTGOING_CHANNEL_STATUS = "/queue/status";

    @Autowired
    private WebSocketConnectionService webSocketConnectionService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private SessionManager sessionManager;

    @GetMapping(value = "/api/user/{user_id}/connection", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebSocketConnections getWebSocketConnections(@PathVariable("user_id") Integer userId) throws Exception {

        if (userId == null || userId < 1) {
            throw new Exception("Bad Request");
        }
        WebSocketConnections webSocketTicket = webSocketConnectionService.getWebSocketConnectionByUserId(userId);

        return webSocketTicket;
    }

    @MessageMapping(INCOMING_CHANNEL_REGISTER)
    @SendToUser(OUTGOING_CHANNEL_STATUS)
    public String registerClient(@Payload WebSocketRequest webSocketRequest, SimpMessageHeaderAccessor headerAccessor) {
        String session = headerAccessor.getSessionId();
        String connection = webSocketRequest.getConnection();
        Integer userId = webSocketRequest.getUserId();

        if (connection == null || userId == null) {
            return StatusEnum.STATUS_UNAUTHORIZED.name();
        }

        WebSocketConnections webSocketConnections = webSocketConnectionService.getWebSocketTicket(connection);
        if (webSocketConnections == null || !userId.equals(webSocketConnections.getUserId())) {
            // no connection or mismatch between connection and user id
            return StatusEnum.STATUS_UNAUTHORIZED.name();
        }
        if (webSocketConnections.hasExpired()) {
            // connection has expired, need to create a new one
            return StatusEnum.STATUS_REFRESH_CONNECTION.name();
        }
        sessionManager.saveSession(session, userId);

        return StatusEnum.STATUS_OK.name();
    }

    @GetMapping(value = "/api/test/{user_id}/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void testWebSocket(@PathVariable("user_id") Integer userId,
                              @PathVariable("message") String message) throws Exception {

        if (userId == null || userId < 1 || !StringUtils.hasText(message)) {
            throw new Exception("Bad Request");
        }
        webSocketService.sendEvent(userId, message);
    }
}
