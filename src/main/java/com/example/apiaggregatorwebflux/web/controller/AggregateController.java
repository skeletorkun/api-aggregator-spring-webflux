package com.example.apiaggregatorwebflux.web.controller;


import com.example.apiaggregatorwebflux.web.model.AggregateDto;
import com.example.apiaggregatorwebflux.web.services.AggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

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
        return this.service.get(pricing, track, shipments)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
