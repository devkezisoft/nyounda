package com.kezisoft.nyounda.application.servicerequest.port.out;

import com.kezisoft.nyounda.domain.servicerequest.Tag;

import java.util.List;
import java.util.UUID;

public interface TagRepository {
    List<Tag> findAllTags(List<UUID> tagIds);

    UUID save(Tag tag);
}
