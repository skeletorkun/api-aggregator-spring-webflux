package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.web.model.PricingDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public interface PricingService {
    Mono<PricingDto> get(List<String> countryCodes);
}
