package com.kezisoft.nyounda.api.provider.request;

import com.kezisoft.nyounda.application.provider.command.ProviderSkillUpdateCommand;

import java.util.UUID;

public record ProviderSkillUpdateRequest(
        UUID providerSkillId,
        UUID categoryId,
        UUID subcategoryId,
        String description
) {
    public ProviderSkillUpdateCommand toCommand() {
        return new ProviderSkillUpdateCommand(
                providerSkillId,
                categoryId,
                subcategoryId,
                description
        );
    }
}
