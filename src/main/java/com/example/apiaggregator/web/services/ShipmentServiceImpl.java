package com.example.apiaggregator.web.services;

import com.example.apiaggregator.web.model.ShipmentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class ShipmentServiceImpl implements ShipmentService {

    private static final String BASE_URL = "http://localhost:8080/shipments";
    private final WebClient client;

    public ShipmentServiceImpl() {
        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

    }

    @Override
    public Mono<ShipmentDto> get(List<String> orderIds) {

        if(orderIds == null || orderIds.isEmpty()) return Mono.empty();

        return this.client
                .get()
                .uri("?q=" + String.join(",", orderIds))
                .retrieve()
                .bodyToMono(ShipmentDto.class)
                .onErrorResume(ex -> {
                    log.error("Failed to retrieve Shipments {}", orderIds, ex);
                    return Mono.empty();
                });
    }

}
