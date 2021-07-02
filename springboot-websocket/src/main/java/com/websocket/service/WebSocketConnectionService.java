package com.websocket.service;

import com.websocket.dao.WebSocketConnectionsRepo;
import com.websocket.model.WebSocketConnections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class WebSocketConnectionService {

    @Autowired
    private WebSocketConnectionsRepo repo;

    private static int expTime = 60;

    public WebSocketConnections getWebSocketTicket(String connection) {
        return repo.findByConnection(connection);
    }

    @Transactional
    public WebSocketConnections getWebSocketConnectionByUserId(Integer userId) {

        WebSocketConnections webSocketTicket = repo.findByUserId(userId);

        if (webSocketTicket == null || webSocketTicket.hasExpired()) {
            // if connection has expired or does not exist, create a new connection for account and save in database
            String connection = createConnection(userId);
            Date expiration = addMinutesToDate(expTime, new Date());
            webSocketTicket = new WebSocketConnections(userId, connection, expiration);
            repo.save(webSocketTicket);
        }

        return webSocketTicket;
    }


    private Date addMinutesToDate(int minutes, Date date) {
        long currentTimeInMillis = date.getTime();
        return new Date(currentTimeInMillis + ((long) minutes) * (60 * 1000));
    }

    private String createConnection(Integer accountId) {
        String ticket = UUID.randomUUID().toString();
        return ticket;
    }
}
