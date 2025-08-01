package com.kezisoft.nyounda.persistence.user;

import com.kezisoft.nyounda.application.user.port.out.UserRepository;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import com.kezisoft.nyounda.persistence.user.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository repository;

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhone(phoneNumber)
                .map(UserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = repository.save(UserEntity.fromDomain(user));
        return userEntity.toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id)
                .map(UserEntity::toDomain);
    }
}
