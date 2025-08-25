package com.kezisoft.nyounda.api.provider.request;

import com.kezisoft.nyounda.application.provider.command.ProviderSkillCreateCommand;

import java.util.UUID;

public record ProviderSkillCreateRequest(
        UUID categoryId,
        UUID subcategoryId,
        String description
) {
    public ProviderSkillCreateCommand toCommand() {
        return new ProviderSkillCreateCommand(
                categoryId,
                subcategoryId,
                description
        );
    }
}
