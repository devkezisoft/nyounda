package com.kezisoft.nyounda.application.user.handler;

import com.kezisoft.nyounda.application.user.command.RegisterUserCommand;
import com.kezisoft.nyounda.application.user.command.UpdateUserCommand;
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
        return userRepository.findByPhone(phoneNumber);
    }

    @Override
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByPhone(String phoneNumber) {
        return userRepository.findByPhone(phoneNumber);
    }

    @Override
    public User registerUser(RegisterUserCommand registerUserCommand) {
        User user = RegisterUserCommand.toDomain(registerUserCommand);
        User savedUser = userRepository.save(user);
        // Automatically send a login pin after registration
        // using notify system to avoid circular dependency
        // notifier.notify(
        //         new GeneratePinCommand(savedUser.getPhoneNumber(), Channel.SMS)
        return savedUser;
    }

    @Override
    public Optional<User> updateUser(UUID userId, UpdateUserCommand command) {
        return userRepository.findById(userId).map(existingUser -> {
            if (command.fullName() != null) {
                existingUser = existingUser.withFullName(command.fullName());
            }
            if (command.email() != null) {
                existingUser = existingUser.withEmail(command.email());
            }
            if (command.phone() != null) {
                existingUser = existingUser.withPhone(command.phone());
            }
            return userRepository.save(existingUser);
        });
    }
}
