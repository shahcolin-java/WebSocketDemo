package com.websocket;

public enum StatusEnum {

    STATUS_OK("ok"), STATUS_REFRESH_CONNECTION("refresh_connection"), STATUS_UNAUTHORIZED("unauthorized"), STATUS_ERROR("error");

    private final String status;

    StatusEnum(String status) {
        this.status = status;
    }
}
