package com.kezisoft.nyounda.application.user.port.in;

import com.kezisoft.nyounda.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    Optional<User> getByPhoneNumber(String phoneNumber);

    Optional<User> getById(UUID id);

    Optional<User> getOrCreateUser(String phoneNumber);


}
