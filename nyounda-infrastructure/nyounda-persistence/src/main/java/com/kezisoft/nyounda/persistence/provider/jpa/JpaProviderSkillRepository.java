package com.kezisoft.nyounda.persistence.provider.jpa;

import com.kezisoft.nyounda.persistence.provider.entity.ProviderSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaProviderSkillRepository extends JpaRepository<ProviderSkillEntity, UUID> {

    @Query("""
            select ps
            from ProviderSkillEntity ps
            join fetch ps.category c
            where ps.provider.id = :providerId
            order by c.name asc
            """)
    List<ProviderSkillEntity> findWithCategoryByProviderId(UUID providerId);
}
