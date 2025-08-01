package com.kezisoft.nyounda.application.user.handler;

import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.application.user.port.out.UserRepository;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserHandler implements UserUseCase {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getOrCreateUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .or(() -> Optional.of(userRepository.save(User.createFromPhoneNumber(phoneNumber))));

    }
}
