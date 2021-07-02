package com.websocket.dao;

import com.websocket.model.WebSocketConnections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketConnectionsRepo extends JpaRepository<WebSocketConnections, Integer> {
    WebSocketConnections findByUserId(Integer accountId);

    WebSocketConnections findByConnection(String ticket);
}
