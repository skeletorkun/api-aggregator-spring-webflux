package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.TrackingDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrackingService {
    Mono<TrackingDto> get(List<String> orderIds);

}
