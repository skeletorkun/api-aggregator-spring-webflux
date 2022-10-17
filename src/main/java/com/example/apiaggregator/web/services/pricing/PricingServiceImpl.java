package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.configuration.JmsConfig;
import com.example.apiaggregator.queue.TimedQueue;
import com.example.apiaggregator.web.model.PricingDto;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.example.apiaggregator.configuration.JmsConfig.PRICING_API_RESULT_QUEUE;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final String BASE_URL = "http://localhost:8080/pricing";
    private final WebClient client;

    private final TimedQueue queue;

    private final Map<String, Double> pricingData;
    private Observable<Map.Entry<String, Double>> observable;

    final JmsTemplate jmsTemplate;

    public PricingServiceImpl(JmsTemplate jmsTemplate) {
        this.client = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        this.queue = new TimedQueue(jmsTemplate, JmsConfig.PRICING_API_QUEUE);
        this.jmsTemplate = jmsTemplate;
        pricingData = new HashMap<>();
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


    @Override
    public Future<PricingDto> fetch(List<String> countryCodes) {
        queue.push(countryCodes);
        Disposable subscribe = observable.subscribe(
                s -> log.info("Observable changed {}", s)
        );
        return null;
    }

    @JmsListener(destination = PRICING_API_RESULT_QUEUE)
    public void handlePricingResult(PricingDto result) {
        log.info("handlePricingResult {}", result);
        pricingData.putAll(result);
        observable = Observable.fromIterable(pricingData.entrySet());
//        ConnectableObservable<Map.Entry<String, Double>> publish = observable.publish();
//        publish.connect(); // ??
    }
}
