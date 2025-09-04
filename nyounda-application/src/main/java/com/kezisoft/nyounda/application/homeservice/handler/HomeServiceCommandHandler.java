package com.kezisoft.nyounda.application.homeservice.handler;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.homeservice.command.CreateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.command.UpdateServiceCommand;
import com.kezisoft.nyounda.application.homeservice.port.in.HomeServiceCommandUseCase;
import com.kezisoft.nyounda.application.homeservice.port.out.HomeServiceRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.TagRepository;
import com.kezisoft.nyounda.application.images.port.out.ImageRepository;
import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
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
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;

    @Override
    @Transactional
    public ServiceRequestId create(CreateServiceCommand command) {
        log.debug("Received CreateServiceCommand:  {}", command);
        ProviderId providerId = ProviderId.valueOf(command.providerId());
        var provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ProviderNotFoundException(providerId));

        CategoryId categoryId = CategoryId.valueOf(command.categoryId());
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        var tags = tagRepository.findAllTags(command.tags());

        var images = imageRepository.findAllImages(command.imageIds());

        ServiceRequest service = CreateServiceCommand.toDomain(command, provider, category, tags, images);
        return homeServiceRepository.save(service);
    }

    @Override
    public void update(ServiceRequestId serviceRequestId, UpdateServiceCommand command) {
    }

    @Override
    public void delete(ServiceRequestId serviceRequestId) {
        homeServiceRepository.deleteById(serviceRequestId);
    }
}