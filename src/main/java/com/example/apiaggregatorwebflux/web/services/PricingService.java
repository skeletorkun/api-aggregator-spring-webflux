package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.PricingDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Future;

public interface PricingService {
    Mono<PricingDto> get(List<String> countryCodes);

    Future<PricingDto> fetch(List<String> countryCodes);
}
