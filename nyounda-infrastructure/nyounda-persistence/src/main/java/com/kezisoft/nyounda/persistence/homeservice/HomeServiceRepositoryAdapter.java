package com.kezisoft.nyounda.persistence.homeservice;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.homeservice.port.out.*;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ProviderNotFoundException;
import com.kezisoft.nyounda.domain.homeservice.CategoryId;
import com.kezisoft.nyounda.domain.homeservice.HomeService;
import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;
import com.kezisoft.nyounda.domain.homeservice.ProviderId;
import com.kezisoft.nyounda.persistence.homeservice.entity.HomeServiceEntity;
import com.kezisoft.nyounda.persistence.homeservice.jpa.JpaHomeServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HomeServiceRepositoryAdapter implements HomeServiceRepository {

    private final JpaHomeServiceRepository repository;

    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;
    private final ServiceImageRepository imageRepository;
    private final CategoriesRepository categoryRepository;
    private final ProviderRepository providerRepository;

    @Override
    public HomeServiceId save(HomeService service) {
        HomeServiceEntity entity = HomeServiceEntity.fromDomain(service);
        HomeServiceEntity saved = repository.save(entity);
        return new HomeServiceId(saved.getId());
    }

    @Override
    public void deleteById(HomeServiceId id) {
        repository.deleteById(id.value());
    }

    @Override
    public Optional<HomeService> findById(HomeServiceId id) {
        return repository.findById(id.value()).flatMap(entity -> {
            var images = imageRepository.findAllImages(entity.getImageIds());
            var tags = tagRepository.findAllTags(entity.getTagIds());
            var reviews = reviewRepository.findAllReviews(entity.getReviewIds());
            var category = categoryRepository.findById(CategoryId.valueOf(entity.getCategoryId()))
                    .orElseThrow(() -> new CategoryNotFoundException(CategoryId.valueOf(entity.getCategoryId())));
            var provider = providerRepository.findById(ProviderId.valueOf(entity.getProviderId()))
                    .orElseThrow(() -> new ProviderNotFoundException(ProviderId.valueOf(entity.getProviderId())));

            return Optional.of(new HomeService(
                    new HomeServiceId(entity.getId()),
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
    public List<HomeService> findAll() {
        return List.of();
    }
}
