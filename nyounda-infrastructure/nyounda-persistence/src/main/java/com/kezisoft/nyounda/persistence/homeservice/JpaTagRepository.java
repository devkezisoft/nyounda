package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.homeservice.port.out.TagRepository;
import com.kezisoft.nyounda.domain.homeservice.Tag;
import com.kezisoft.nyounda.persistence.homeservice.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaTagRepository implements TagRepository {

    private final SpringJpaTagRepository repository;

    @Override
    public List<Tag> findAllTags(List<UUID> tagIds) {
        return repository.findAllById(tagIds).stream()
                .map(TagEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UUID save(Tag tag) {
        var tagEntity = TagEntity.fromDomain(tag);
        return repository.save(tagEntity).getId();
    }
}
