package com.example.apiaggregator.web.controller;


import com.example.apiaggregator.web.model.AggregateDto;
import com.example.apiaggregator.web.services.AggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/aggregation")
public class AggregateController {

    private final AggregationService service;

    @GetMapping
    public Mono<ResponseEntity<AggregateDto>> getAggregation(
            @RequestParam(required = false) List<String> shipments,
            @RequestParam(required = false) List<String> track,
            @RequestParam(required = false) List<String> pricing
    ) {
        log.info("Get aggregate is called with params {}, {}, {}", shipments, track, pricing);
        Instant start = Instant.now();
        Mono<ResponseEntity<AggregateDto>> response = this.service.get(pricing, track, shipments)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
        log.info("Get aggregate returns with result {} in {} ms", response, Duration.between(Instant.now(), start));
        return response;
    }

}
