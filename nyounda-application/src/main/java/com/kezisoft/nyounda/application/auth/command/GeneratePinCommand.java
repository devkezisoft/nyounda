package com.kezisoft.nyounda.application.auth.command;

import com.kezisoft.nyounda.domain.auth.Channel;

public record GeneratePinCommand(
        String phone, Channel channel
) {
    public GeneratePinCommand {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null");
        }
    }

}
