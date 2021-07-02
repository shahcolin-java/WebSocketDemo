package com.websocket.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "web_socket_connections")
public class WebSocketConnections {

    private static final long serialVersionUID = 3024130620065991062L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "connection", nullable = false)
    private String connection;

    @CreationTimestamp
    @Column(name = "created", insertable = false, updatable = false)
    private Date created;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    private Date updated;

    @Column(name = "expiration", nullable = false)
    private Date expiration;

    public WebSocketConnections() {
    }

    public WebSocketConnections(Integer userId, String connection, Date expiration) {
        this.userId = userId;
        this.connection = connection;
        this.expiration = expiration;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public boolean hasExpired() {
        long diffInMills = new Date().getTime() - getExpiration().getTime();
        return diffInMills / 1000 > 0;
    }

    @Override
    public int hashCode() {
        return getUserId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebSocketConnections)) return false;
        WebSocketConnections wst = (WebSocketConnections) o;
        return getUserId().equals(wst.getUserId());
    }
}