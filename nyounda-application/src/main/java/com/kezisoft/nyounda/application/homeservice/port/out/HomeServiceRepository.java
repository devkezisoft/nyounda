package com.kezisoft.nyounda.application.homeservice.port.out;

import com.kezisoft.nyounda.domain.homeservice.HomeService;
import com.kezisoft.nyounda.domain.homeservice.HomeServiceId;

import java.util.List;
import java.util.Optional;

public interface HomeServiceRepository {

    HomeServiceId save(HomeService service);

    void deleteById(HomeServiceId id);

    Optional<HomeService> findById(HomeServiceId id);

    List<HomeService> findAll();
}