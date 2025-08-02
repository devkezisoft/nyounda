package com.kezisoft.nyounda.api.account;


import com.kezisoft.nyounda.api.account.view.AccountView;
import com.kezisoft.nyounda.application.user.port.out.AccountUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AccountView getAccount() {
        return accountUseCase
                .getCurrentUser()
                .map(AccountView::fromDomain)
                .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

}
