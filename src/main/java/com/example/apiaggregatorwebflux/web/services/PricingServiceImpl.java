package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.PricingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final String BASE_URL = "http://localhost:8080/pricing";
    private final WebClient client;
    private final RestTemplate restTemplate;

    public PricingServiceImpl() {
        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        this.restTemplate = new RestTemplate();

    }

    @Override
    public Mono<PricingDto> get(List<String> countryCodes) {

        if (countryCodes == null || countryCodes.isEmpty()) return Mono.empty();

        return this.client
                .get()
                .uri("?q=" + String.join(",", countryCodes))
                .retrieve()
                .bodyToMono(PricingDto.class)
                .onErrorResume(ex -> {
                    log.error("Failed to retrieve Shipments {}", countryCodes, ex);
                    return Mono.empty();
                });
    }

    @Async
    @Override
    public Future<PricingDto> fetch(List<String> countryCodes) {
        return CompletableFuture.completedFuture(
                this.restTemplate
                        .getForObject("?q=" + String.join(",", countryCodes), PricingDto.class)
        );
    }
}
