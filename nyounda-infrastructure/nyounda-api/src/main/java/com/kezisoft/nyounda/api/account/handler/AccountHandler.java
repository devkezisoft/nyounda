package com.kezisoft.nyounda.api.account.handler;

import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.application.user.port.out.AccountUseCase;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountHandler implements AccountUseCase {
    private final UserUseCase userUseCase;

    @Override
    public Optional<User> getCurrentUser() {
        log.debug("Request to get current user");
        return SecurityUtils.getCurrentUserLogin().flatMap(id -> userUseCase.getById(UUID.fromString(id)));
    }
}
