package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.queue.PublisherQueue;
import com.example.apiaggregator.web.model.PricingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final String BASE_URL = "http://localhost:8080/pricing";
    private final WebClient client;
    private final RestTemplate restTemplate;
    private final PublisherQueue<PricingDto> publisherQueue;

    public PricingServiceImpl() {

        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        this.restTemplate = new RestTemplate();

        this.publisherQueue = new PublisherQueue<PricingDto>((list) -> Mono.create(sink -> sink.success(this.restTemplate
                                            .getForObject("?q=" + String.join(",", list), PricingDto.class))));

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


    @Async("asyncExecutor")
    @Override
    public Mono<PricingDto> fetch(List<String> countryCodes) {

        //sub to queue

        // pub items

        // in callback, make the rest template call and return

        final var sink = Sinks.<PricingDto>one();

        final var disposable = publisherQueue.subscribe(dto -> {
            // check if all of them retrieved
            sink.tryEmitValue(dto);
        });

        publisherQueue.push(countryCodes);

        return sink.asMono().doOnTerminate(disposable::dispose);
    }
}
