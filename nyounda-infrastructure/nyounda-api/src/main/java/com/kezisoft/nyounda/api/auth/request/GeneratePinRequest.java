package com.kezisoft.nyounda.api.auth.request;

import com.kezisoft.nyounda.application.auth.command.GeneratePinCommand;
import com.kezisoft.nyounda.domain.auth.Channel;

public record GeneratePinRequest(
        String phone,
        Channel channel
) {

    public GeneratePinCommand toCommand() {
        return new GeneratePinCommand(phone, channel);
    }
}
