package com.example.apiaggregator.web.services.shipment;

import com.example.apiaggregator.web.model.ShipmentDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ShipmentService {

    Mono<ShipmentDto> get(List<String> orderIds);

}
