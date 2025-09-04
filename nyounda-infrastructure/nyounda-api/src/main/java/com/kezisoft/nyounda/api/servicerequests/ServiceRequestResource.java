package com.kezisoft.nyounda.api.servicerequests;

import com.kezisoft.nyounda.api.security.SecurityUtils;
import com.kezisoft.nyounda.api.servicerequests.request.CreateRequest;
import com.kezisoft.nyounda.api.servicerequests.request.UpdateRequest;
import com.kezisoft.nyounda.api.servicerequests.response.ServiceRequestView;
import com.kezisoft.nyounda.application.servicerequest.port.in.ServiceRequestUseCase;
import com.kezisoft.nyounda.application.shared.exception.AccountNotFoundException;
import com.kezisoft.nyounda.application.shared.exception.ServiceRequestNotFoundException;
import com.kezisoft.nyounda.domain.servicerequest.ServiceRequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ServiceRequestResource {

    private final ServiceRequestUseCase serviceRequestUseCase;

    @PostMapping
    public ResponseEntity<ServiceRequestView> create(@RequestBody CreateRequest req) {
        UUID currentUserId = SecurityUtils.getCurrentUserLogin()
                .map(UUID::fromString)
                .orElseThrow(AccountNotFoundException::new);
        var serviceRequest = serviceRequestUseCase.create(req.toCommand(currentUserId));
        var view = ServiceRequestView.from(serviceRequest);
        return ResponseEntity
                .created(URI.create("/api/requests/" + serviceRequest.id().value()))
                .body(view);
    }

    @GetMapping
    public List<ServiceRequestView> findAllByCurrentUser() {
        return SecurityUtils.getCurrentUserLogin().map(id ->
                        serviceRequestUseCase.findAllByUserId(UUID.fromString(id))
                                .stream()
                                .map(ServiceRequestView::from)
                                .toList()
                )
                .orElseThrow(AccountNotFoundException::new);
    }

    @GetMapping("/{id}")
    public ServiceRequestView findById(@PathVariable UUID id) {
        return serviceRequestUseCase.findById(ServiceRequestId.valueOf(id))
                .map(ServiceRequestView::from)
                .orElseThrow(ServiceRequestNotFoundException::new);
    }

    @PutMapping("/{id}")
    public ServiceRequestView update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        UUID currentUserId = SecurityUtils.getCurrentUserLogin()
                .map(UUID::fromString)
                .orElseThrow(AccountNotFoundException::new);
        return serviceRequestUseCase.update(ServiceRequestId.valueOf(id), req.toCommand(currentUserId))
                .map(ServiceRequestView::from)
                .orElseThrow(ServiceRequestNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceRequestUseCase.delete(ServiceRequestId.valueOf(id));
        return ResponseEntity.noContent().build();
    }
}
