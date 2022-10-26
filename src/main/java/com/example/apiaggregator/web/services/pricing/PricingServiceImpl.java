package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.queue.PublisherQueue;
import com.example.apiaggregator.web.model.PricingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final String BASE_URL = "http://localhost:8080/pricing";
    private final WebClient client;
    private final PublisherQueue<PricingDto> publisherQueue;

    public PricingServiceImpl() {

        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        this.publisherQueue = new PublisherQueue<>(this::getFromApi);
    }

    public Mono<PricingDto> getFromApi(List<String> countryCodes) {

        if (countryCodes == null || countryCodes.isEmpty()) return Mono.empty();
        log.info("Called getFromApi for codes {}", countryCodes);
        return this.client
                .get()
                .uri("?q=" + String.join(",", countryCodes))
                .retrieve()
                .bodyToMono(PricingDto.class)
                .onErrorResume(ex -> {
                    log.error("Failed to retrieve Shipments {}", countryCodes, ex);
                    return Mono.just(new PricingDto(true));
                });
    }

    @Override
    public Mono<PricingDto> get(List<String> countryCodes) {

        final var sink = Sinks.<PricingDto>one();
        final var result = new PricingDto();

        final var disposable = publisherQueue.subscribe(dto -> {
            log.info("Response arrived !! {}", dto);
            dto.entrySet().stream().filter(e -> countryCodes.contains(e.getKey()))
                    .forEach(e -> result.put(e.getKey(), e.getValue()));
            if (Boolean.TRUE.equals(dto.getHasError())) {
                log.error("Something went wrong while processing {}", countryCodes);
                sink.tryEmitError(new RuntimeException("ALERT!!!"));
            }
            else if (result.keySet().containsAll(countryCodes)) {
                log.error("All results retrieved for {}", countryCodes);
                sink.tryEmitValue(result);
            }
        });

        publisherQueue.push(countryCodes);

        return sink.asMono()
                .doOnTerminate(disposable::dispose).timeout(Duration.ofSeconds(10));
    }
}
