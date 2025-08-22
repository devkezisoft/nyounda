package com.kezisoft.nyounda.api.provider;

import com.kezisoft.nyounda.api.provider.view.ProviderView;
import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.application.provider.port.in.ProviderUseCase;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderResource {

    private final ProviderUseCase providerUseCase;

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping
    public ProviderView getProvider() {
        log.debug("Fetching provider for current user");
        return SecurityUtils.getCurrentUserLogin().flatMap(id -> providerUseCase.getByUserId(UUID.fromString(id)))
                .map(ProviderView::fromDomain)
                .orElseThrow(ProviderNotFoundException::new);
    }

}
