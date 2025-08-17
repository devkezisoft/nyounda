package com.kezisoft.nyounda.application.homeservice.handler;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.homeservice.command.CreateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.command.UpdateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.port.in.HomeServiceCommandUseCase;
import com.kezisoft.nyounda.application.homeservice.port.out.HomeServiceRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.ServiceImageRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.TagRepository;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;
import com.kezisoft.nyounda.domain.homeservice.HomeService;
import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;
import com.kezisoft.nyounda.domain.homeservice.ProviderId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeServiceCommandHandler implements HomeServiceCommandUseCase {

    private final HomeServiceRepository homeServiceRepository;
    private final ProviderRepository providerRepository;
    private final CategoriesRepository categoryRepository;
    private final ServiceImageRepository serviceImageRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public HomeServiceId create(CreateServiceCommand command) {
        log.debug("Received CreateServiceCommand:  {}", command);
        ProviderId providerId = ProviderId.valueOf(command.providerId());
        var provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException(providerId));

        CategoryId categoryId = CategoryId.valueOf(command.categoryId());
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        var tags = tagRepository.findAllTags(command.tags());

        var images = serviceImageRepository.findAllImages(command.imageIds());

        HomeService service = CreateServiceCommand.toDomain(command, provider, category, tags, images);
        return homeServiceRepository.save(service);
    }

    @Override
    public void update(HomeServiceId homeServiceId, UpdateServiceCommand command) {
    }

    @Override
    public void delete(HomeServiceId homeServiceId) {
        homeServiceRepository.deleteById(homeServiceId);
    }
}