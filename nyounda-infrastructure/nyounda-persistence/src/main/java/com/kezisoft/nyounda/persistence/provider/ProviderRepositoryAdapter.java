package com.kezisoft.nyounda.persistence.provider;

import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.domain.provider.Provider;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.provider.ProviderSkill;
import com.kezisoft.nyounda.persistence.provider.entity.ProviderEntity;
import com.kezisoft.nyounda.persistence.provider.entity.ProviderSkillEntity;
import com.kezisoft.nyounda.persistence.provider.jpa.JpaProviderRepository;
import com.kezisoft.nyounda.persistence.provider.jpa.JpaProviderSkillRepository;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import com.kezisoft.nyounda.persistence.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProviderRepositoryAdapter implements ProviderRepository {

    private final JpaProviderRepository jpaProviderRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProviderSkillRepository jpaProviderSkillRepository;

    @Override
    public Optional<Provider> findById(ProviderId id) {
        List<ProviderSkill> skills = jpaProviderSkillRepository.findWithCategoryByProviderId(id.value())
                .stream()
                .map(ProviderSkillEntity::toDomain)
                .toList();
        return jpaProviderRepository.findById(id.value())
                .map(entity -> entity.toDomain(skills));
    }

    @Override
    public Optional<Provider> findByUserId(UUID userId) {
        log.debug("Finding provider by userId: {}", userId);
        return jpaProviderRepository.findByUserId(userId)
                .map(entity -> {
                    List<ProviderSkill> skills = jpaProviderSkillRepository.findWithCategoryByProviderId(entity.getId())
                            .stream()
                            .map(ProviderSkillEntity::toDomain)
                            .toList();
                    log.debug("Found provider: {} with skills: {}", entity, skills);
                    return entity.toDomain(skills);
                });
    }

    @Override
    @Transactional
    public Provider save(UUID userId, Provider provider) {
        UserEntity user = jpaUserRepository.findById(userId)
                .orElseThrow(AccountNotFoundException::new);
        ProviderEntity entity = ProviderEntity.fromDomain(provider, user);
        Provider domain = jpaProviderRepository.save(entity).toDomain(List.of());
        List<ProviderSkillEntity> skillEntities = provider.skills().stream()
                .map(skill -> ProviderSkillEntity.fromDomain(skill, domain.id().value()))
                .toList();
        List<ProviderSkillEntity> providerSkillEntities = jpaProviderSkillRepository.saveAll(skillEntities);
        return domain.withSkills(providerSkillEntities.stream()
                .map(ProviderSkillEntity::toDomain)
                .toList());
    }

    @Override
    @Transactional
    public void deleteAllProviderSkills(List<UUID> skillIds) {
        jpaProviderSkillRepository.deleteAllById(skillIds);
    }
}
