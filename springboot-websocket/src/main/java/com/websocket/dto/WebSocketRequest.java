package com.websocket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketRequest {

    @JsonProperty
    private String connection;

    @JsonProperty("user_id")
    private Integer userId;

    public WebSocketRequest() {
    }

    public WebSocketRequest(String connection) {
        this.connection = connection;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSocketRequest)) return false;

        WebSocketRequest that = (WebSocketRequest) o;
        return !(getConnection() != null ? !getConnection().equals(that.getConnection()) : that.getConnection() != null);
    }

    @Override
    public int hashCode() {
        return connection.hashCode();
    }
}
