package com.kezisoft.nyounda.application.servicerequest.handler;

import com.kezisoft.nyounda.application.categories.port.out.CategoriesRepository;
import com.kezisoft.nyounda.application.images.port.in.ImageUseCase;
import com.kezisoft.nyounda.application.servicerequest.command.ServiceRequestCreateCommand;
import com.kezisoft.nyounda.application.servicerequest.command.UpdateServiceCommand;
import com.kezisoft.nyounda.application.servicerequest.port.in.ServiceRequestUseCase;
import com.kezisoft.nyounda.application.servicerequest.port.out.OfferReadPort;
import com.kezisoft.nyounda.application.servicerequest.port.out.ServiceRequestRepository;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.CategoryNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ServiceRequestNotFoundException;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.domain.servicerequest.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceRequestHandler implements ServiceRequestUseCase {

    private final ServiceRequestRepository serviceRequestRepository;
    private final CategoriesRepository categoryRepository;
    private final OfferReadPort offerReadPort;
    private final ImageUseCase imageUseCase;
    private final UserUseCase userUseCase;

    @Override
    @Transactional
    public ServiceRequest create(ServiceRequestCreateCommand command) {
        log.debug("Received ServiceRequestCreateCommand:  {}", command);
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        var subCategory = categoryRepository.findById(command.subCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.subCategoryId()));

        var user = userUseCase.getById(command.userId())
                .orElseThrow(AccountNotFoundException::new);

        var images = imageUseCase.findAllImages(command.imageIds());

        ServiceRequest service = ServiceRequestCreateCommand.toDomain(command, ServiceRequestStatus.PENDING, category, subCategory, images, user);
        return serviceRequestRepository.save(service);
    }

    @Override
    @Transactional
    public Optional<ServiceRequest> update(ServiceRequestId id, UpdateServiceCommand command) {
        log.debug("Updating service request id: {} with command: {}", id, command);
        return serviceRequestRepository.findById(id).map(exist -> {

            if (command.title() != null) {
                exist = exist.withTitle(command.title());
            }
            if (command.description() != null) {
                exist = exist.withDescription(command.description());
            }
            if (command.addressText() != null) {
                exist = exist.withAddress(command.addressText());
            }

            var category = categoryRepository.findById(command.categoryId())
                    .orElse(exist.category());
            exist = exist.withCategory(category);
            var subCategory = categoryRepository.findById(command.subCategoryId())
                    .orElse(exist.subcategory());
            exist = exist.withSubcategory(subCategory);

            if (command.imageIds() != null) {
                // compute detach/attach
                Set<UUID> newIds = new HashSet<>(command.imageIds());
                Set<UUID> oldIds = exist.images().stream().map(Image::id).collect(HashSet::new, Set::add, Set::addAll);

                // remove images not in newIds
                var toRemove = exist.images().stream().filter(img -> !newIds.contains(img.id())).toList();
                ServiceRequest finalExist = exist;
                toRemove.forEach(img -> {
                    finalExist.images().remove(img);
                    // also remove the physical blob + DB row
                    imageUseCase.delete(img.id());
                });

                // add newly referenced images
                var toAddIds = newIds.stream().filter(id2 -> !oldIds.contains(id2)).toList();
                if (!toAddIds.isEmpty()) {
                    var addEntities = imageUseCase.findAllImages(toAddIds);
                    if (addEntities.size() != toAddIds.size()) {
                        throw new NoSuchElementException("One or more imageIds not found (adding)");
                    }
                    exist.images().addAll(addEntities);
                }
            }
            return serviceRequestRepository.save(exist);
        });
    }

    @Override
    public void delete(UUID currentUserId, ServiceRequestId id) throws AccessDeniedException {
        log.debug("Deleting service request id: {} by user id: {}", id, currentUserId);
        var exist = serviceRequestRepository.findById(id)
                .orElseThrow(ServiceRequestNotFoundException::new);

        if (exist.isNotOwnedBy(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to delete this request.");
        }

        // collect attached images first
        var imgIds = exist.imageIds();

        serviceRequestRepository.deleteById(id);
        // remove physical blobs + image rows after commit
        imageUseCase.delete(imgIds);
    }

    @Override
    public List<ServiceRequest> findAllByUserId(UUID userId) {
        log.debug("Fetching all service requests for user id: {}", userId);
        var user = userUseCase.getById(userId).orElseThrow(AccountNotFoundException::new);
        return serviceRequestRepository.findAllByUser(user);
    }

    @Override
    public Optional<ServiceRequest> findById(ServiceRequestId id) {
        log.debug("Fetching service request by id: {}", id);
        return serviceRequestRepository.findById(id);
    }

    @Override
    public boolean hasUserAlreadyApplied(UUID userId, ServiceRequestId serviceRequestId) {
        return offerReadPort.existsOfferForRequestAndUser(
                serviceRequestId.value(), userId
        );
    }

    @Override
    public List<OfferCandidateView> findCandidates(ServiceRequestId requestId) {
        return offerReadPort.findCandidates(requestId);
    }

    @Override
    public void setChosenOffer(ServiceRequestId serviceRequestId, UUID offerId) {
        serviceRequestRepository.setChosenOffer(serviceRequestId, offerId);
    }

}