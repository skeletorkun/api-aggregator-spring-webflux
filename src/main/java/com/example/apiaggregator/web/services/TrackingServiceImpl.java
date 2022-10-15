package com.example.apiaggregator.web.services;

import com.example.apiaggregator.web.model.TrackingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class TrackingServiceImpl implements TrackingService {

    private static final String BASE_URL = "http://localhost:8080/track";
    private final WebClient client;

    public TrackingServiceImpl() {
        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

    }

    @Override
    public Mono<TrackingDto> get(List<String> orderIds) {

        if(orderIds == null || orderIds.isEmpty()) return Mono.empty();

        return this.client
                .get()
                .uri("?q=" + String.join(",", orderIds))
                .retrieve()
                .bodyToMono(TrackingDto.class)
                .onErrorResume(ex -> {
                    log.error("Failed to retrieve Tracking {}", orderIds, ex);
                    return Mono.empty();
                });
    }

}
