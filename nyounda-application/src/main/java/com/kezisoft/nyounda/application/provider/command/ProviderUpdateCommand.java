package com.kezisoft.nyounda.application.provider.command;

import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;

import java.util.List;
import java.util.UUID;

public record ProviderUpdateCommand(
        List<ProviderSkillUpdateCommand> skills,
        String location
) {
    public Provider toDomain(List<ProviderSkill> skills) {
        return new Provider(
                ProviderId.valueOf(UUID.randomUUID()),
                null,
                null,
                0.0,
                0,
                location,
                0,
                0,
                null,
                skills
        );
    }
}