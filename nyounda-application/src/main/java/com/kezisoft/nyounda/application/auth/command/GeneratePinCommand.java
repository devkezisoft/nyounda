package com.kezisoft.nyounda.application.auth.command;

import com.kezisoft.nyounda.domain.auth.Channel;

public record GeneratePinCommand(
        String phoneNumber, Channel channel
) {
}
