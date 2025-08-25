package com.kezisoft.nyounda.api.provider;

import com.kezisoft.nyounda.api.provider.request.ProviderCreateRequest;
import com.kezisoft.nyounda.api.provider.view.ProviderView;
import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.application.provider.port.in.ProviderUseCase;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.provider.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;
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

    /**
     * {@code POST  /providers} : register provider.
     *
     * @return the current provider.
     */
    @PostMapping
    public ResponseEntity<ProviderView> createProvider(@RequestBody ProviderCreateRequest request) {
        log.debug("Creating provider for current user");
        return SecurityUtils.getCurrentUserLogin().map(id ->
                {
                    Provider provider = providerUseCase.create(UUID.fromString(id), request.toCommand());
                    ProviderView providerView = ProviderView.fromDomain(provider);
                    log.debug("Created provider: {}", provider);
                    try {
                        return ResponseEntity.created(new URI("/api/providers/" + provider.id()))
                                .headers(HeaderUtil
                                        .createEntityCreationAlert(
                                                "nyounda-api", true,
                                                "account", providerView.id().toString())
                                )
                                .body(providerView);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(ProviderNotFoundException::new);


    }

}
