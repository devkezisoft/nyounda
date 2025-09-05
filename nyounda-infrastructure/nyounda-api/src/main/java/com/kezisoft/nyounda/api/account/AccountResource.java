package com.kezisoft.nyounda.api.account;


import com.kezisoft.nyounda.api.account.request.AccountCreateRequest;
import com.kezisoft.nyounda.api.account.request.AccountUpdateRequest;
import com.kezisoft.nyounda.api.account.view.AccountView;
import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserUseCase userUseCase;

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/accounts")
    public AccountView getAccount() {
        return SecurityUtils.getCurrentUserLogin().flatMap(id -> userUseCase.getById(UUID.fromString(id)))
                .map(AccountView::fromDomain)
                .orElseThrow(AccountNotFoundException::new);
    }

    /**
     * {@code POST  /account} : register user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @PostMapping("/register")
    public ResponseEntity<AccountView> registerAccount(@RequestBody AccountCreateRequest request) throws URISyntaxException {
        log.debug("Registering user account: {}", request);
        User user = userUseCase
                .registerUser(request.toCommand());
        AccountView accountView = AccountView.fromDomain(user);
        return ResponseEntity.created(new URI("/api/accounts/" + accountView.id()))
                .headers(HeaderUtil
                        .createEntityCreationAlert(
                                "nyounda-api", true,
                                "account", accountView.id().toString())
                )
                .body(accountView);
    }

    /**
     * {@code PATCH  /account} : update user.
     *
     * @param request the user to update.
     * @return the ResponseEntity with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the user has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping("/accounts")
    public AccountView updateAccount(@RequestBody AccountUpdateRequest request) throws URISyntaxException {
        log.debug("Updating user account: {}", request);
        UUID currentUserId = SecurityUtils.getCurrentUserLogin()
                .map(UUID::fromString)
                .orElseThrow(AccountNotFoundException::new);
        User user = userUseCase.updateUser(currentUserId, request.toCommand())
                .orElseThrow(AccountNotFoundException::new);
        return AccountView.fromDomain(user);
    }

}
