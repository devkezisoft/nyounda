package com.kezisoft.nyounda.domain.auth;

public enum Channel {
    SMS("sms"),
    CALL("call"),
    EMAIL("email"),
    WHATSAPP("whatsapp"),
    SNA("sna");

    private final String value;

    private Channel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}