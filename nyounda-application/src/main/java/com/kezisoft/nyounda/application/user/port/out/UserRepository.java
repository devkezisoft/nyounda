package com.kezisoft.nyounda.application.user.port.out;

import com.kezisoft.nyounda.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByPhoneNumber(String phoneNumber);

    User save(User user);

    Optional<User> findById(UUID id);
}
