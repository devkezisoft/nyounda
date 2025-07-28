package com.kezisoft.nyounda.persistence.homeservice.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    private String avatarUrl;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Column(name = "roles", columnDefinition = "text[]")
    @Type(ListArrayType.class)
    private List<String> roles;
}
