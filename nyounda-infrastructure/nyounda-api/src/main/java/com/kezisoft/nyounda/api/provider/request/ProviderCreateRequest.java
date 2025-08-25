package com.kezisoft.nyounda.api.provider.request;

import com.kezisoft.nyounda.application.provider.command.ProviderCreateCommand;

import java.util.List;

public record ProviderCreateRequest(
        List<ProviderSkillCreateRequest> skills
) {
    public ProviderCreateCommand toCommand() {
        return new ProviderCreateCommand(
                skills.stream()
                        .map(ProviderSkillCreateRequest::toCommand)
                        .toList()
        );
    }
}
