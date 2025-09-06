package com.kezisoft.nyounda.api.provider.request;

import com.kezisoft.nyounda.application.provider.command.ProviderUpdateCommand;

import java.util.List;

public record ProviderUpdateRequest(
        List<ProviderSkillUpdateRequest> skills,
        String location
) {
    public ProviderUpdateCommand toCommand() {
        return new ProviderUpdateCommand(
                skills.stream()
                        .map(ProviderSkillUpdateRequest::toCommand)
                        .toList(),
                location
        );
    }
}
