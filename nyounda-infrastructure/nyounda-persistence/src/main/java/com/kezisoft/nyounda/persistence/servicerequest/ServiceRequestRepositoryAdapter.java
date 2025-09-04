package com.kezisoft.nyounda.persistence.servicerequest;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.HomeServiceRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.ReviewRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.TagRepository;
import com.kezisoft.nyounda.application.images.port.out.ImageRepository;
import com.kezisoft.nyounda.application.provider.port.out.ProviderRepository;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.categories.CategoryId;
import com.kezisoft.nyounda.domain.provider.ProviderId;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequest;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import com.kezisoft.nyounda.persistence.servicerequest.entity.ServiceRequestEntity;
import com.kezisoft.nyounda.persistence.servicerequest.jpa.JpaServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ServiceRequestRepositoryAdapter implements HomeServiceRepository {

    private final JpaServiceRequestRepository repository;

    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final CategoriesRepository categoryRepository;
    private final ProviderRepository providerRepository;

    @Override
    public ServiceRequestId save(ServiceRequest service) {
        ServiceRequestEntity entity = ServiceRequestEntity.fromDomain(service);
        ServiceRequestEntity saved = repository.save(entity);
        return new ServiceRequestId(saved.getId());
    }

    @Override
    public void deleteById(ServiceRequestId id) {
        repository.deleteById(id.value());
    }

    @Override
    public Optional<ServiceRequest> findById(ServiceRequestId id) {
        return repository.findById(id.value()).flatMap(entity -> {
            var images = imageRepository.findAllImages(entity.getImageIds());
            var tags = tagRepository.findAllTags(entity.getTagIds());
            var reviews = reviewRepository.findAllReviews(entity.getReviewIds());
            var category = categoryRepository.findById(CategoryId.valueOf(entity.getCategoryId()))
                    .orElseThrow(() -> new CategoryNotFoundException(CategoryId.valueOf(entity.getCategoryId())));
            var provider = providerRepository.findById(ProviderId.valueOf(entity.getProviderId()))
                    .orElseThrow(() -> new ProviderNotFoundException(ProviderId.valueOf(entity.getProviderId())));

            return Optional.of(new ServiceRequest(
                    new ServiceRequestId(entity.getId()),
                    entity.getTitle(),
                    entity.getDescription(),
                    entity.getPricingType(),
                    entity.getPrice(),
                    entity.getMinimumToAcceptBooking(),
                    provider,
                    images,
                    category,
                    tags,
                    entity.getAvailabilityDays().stream().map(DayOfWeek::valueOf).toList(),
                    reviews
            ));
        });
    }

    @Override
    public List<ServiceRequest> findAll() {
        return List.of();
    }
}
