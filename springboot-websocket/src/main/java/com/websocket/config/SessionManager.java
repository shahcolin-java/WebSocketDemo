package com.websocket.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SessionManager implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static Map<Integer, List<String>> userSessionMap = new HashMap<>();
    // reverse session user mapping for fast deletion
    private static Map<String, Integer> sessionUserMap = new HashMap<>();

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        // disconnect session
        String sessionId = event.getSessionId();
        logger.debug("Session disconnect: " + sessionId);
        removeSession(sessionId);
    }

    public List<String> getSessionsByUserId(Integer userId) {
        return userSessionMap.get(userId);
    }

    public void saveSession(String sessionId, Integer accountId) {
        logger.debug("About to link session {} with account {}", sessionId, accountId);
        if (sessionId == null || accountId == null) return;

        List<String> mapEntry = userSessionMap.get(accountId);
        if (mapEntry == null || mapEntry.isEmpty()) {
            // no session for account - put session
            userSessionMap.put(accountId, new ArrayList<>(Collections.singletonList(sessionId)));
        } else {
            // already exists - add session to list
            if (!mapEntry.contains(sessionId)) {
                mapEntry.add(sessionId);
            }
        }
        // always try to add the reverse mapping
        sessionUserMap.put(sessionId, accountId);
    }

    public void removeSession(String sessionId) {
        Integer accountId = sessionId != null ? sessionUserMap.get(sessionId) : null;
        if (accountId == null) {
            // nothing to do
            return;
        }
        logger.debug("About to unlink session {} from account {}" + sessionId);
        sessionUserMap.remove(sessionId);

        List<String> mapEntry = userSessionMap.get(accountId);
        if (mapEntry != null) {
            mapEntry.remove(sessionId);
            if (mapEntry.isEmpty()) {
                userSessionMap.remove(accountId);
            }
        }
    }

}