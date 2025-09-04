package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.servicerequest.port.out.TagRepository;
import com.kezisoft.nyounda.domain.servicerequest.Tag;
import com.kezisoft.nyounda.persistence.servicerequest.entity.TagEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepository {

    private final JpaTagRepository repository;

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
