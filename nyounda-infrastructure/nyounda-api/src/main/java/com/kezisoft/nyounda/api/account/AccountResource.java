package com.kezisoft.nyounda.api.account;


import com.kezisoft.nyounda.api.account.request.AccountCreateRequest;
import com.kezisoft.nyounda.api.account.view.AccountView;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.application.user.port.out.AccountUseCase;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {
    static class AccountResourceException extends RuntimeException {

        AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final AccountUseCase accountUseCase;
    private final UserUseCase userUseCase;

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/accounts")
    public AccountView getAccount() {
        return accountUseCase
                .getCurrentUser()
                .map(AccountView::fromDomain)
                .orElseThrow(() -> new AccountResourceException("User could not be found"));
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

}
