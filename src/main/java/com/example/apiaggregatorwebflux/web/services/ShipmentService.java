package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.ShipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ShipmentService {

    Mono<ShipmentDto> get(List<String> orderIds);

}
