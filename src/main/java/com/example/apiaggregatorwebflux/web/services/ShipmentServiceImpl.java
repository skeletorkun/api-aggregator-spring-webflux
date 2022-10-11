package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.Product;
import com.example.apiaggregatorwebflux.web.model.ShipmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
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
