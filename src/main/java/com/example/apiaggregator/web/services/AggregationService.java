package com.example.apiaggregator.web.services;

import com.example.apiaggregator.web.model.AggregateDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AggregationService {
    Mono<AggregateDto> get(List<String> pricing, List<String> track, List<String> shipments);
}
