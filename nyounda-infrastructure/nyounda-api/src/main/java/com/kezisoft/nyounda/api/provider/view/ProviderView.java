package com.kezisoft.nyounda.api.provider.view;

import com.kezisoft.nyounda.domain.provider.Provider;

public record ProviderView() {
    public static ProviderView fromDomain(Provider provider) {
        return new ProviderView();
    }
}
