package com.kezisoft.nyounda.api.provider.request;

import com.kezisoft.nyounda.application.provider.command.ProviderUpdateCommand;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public record ProviderUpdateRequest(
        @Nullable List<ProviderSkillUpdateRequest> skills,
        String location
) {
    public ProviderUpdateCommand toCommand() {
        return new ProviderUpdateCommand(
                CollectionUtils.isEmpty(skills) ? List.of() :
                        skills.stream()
                                .map(ProviderSkillUpdateRequest::toCommand)
                                .toList(),
                location
        );
    }
}
