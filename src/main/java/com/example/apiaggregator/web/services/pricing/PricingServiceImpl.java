package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.queue.PublisherQueue;
import com.example.apiaggregator.web.model.PricingDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final String BASE_URL = "http://localhost:8080/pricing";
    private final RestTemplate restTemplate;
    private final PublisherQueue countryCodesQueue;

    private final Sinks.Many<PricingDto> pricingResultSink;
    private final ConnectableFlux<PricingDto> pricingDtoFlux;

    public PricingServiceImpl() {

        this.restTemplate = new RestTemplate();

        // A flux for the throttling queue
        this.countryCodesQueue = new PublisherQueue();
        this.countryCodesQueue.subscribe(this::callPricingApi);

        // Another flux - for the API call results
        this.pricingResultSink = Sinks.many().unicast().onBackpressureBuffer();
        this.pricingDtoFlux = pricingResultSink.asFlux().publish();
        this.pricingDtoFlux.connect();

    }

    private void callPricingApi(List<String> list) {
        PricingDto pricingDto = this.restTemplate
                .getForObject("?q=" + String.join(",", list), PricingDto.class);


        // update the collection and notify listeners
        pricingResultSink.tryEmitNext(pricingDto); // todo handle error
    }

    @Async("asyncExecutor")
    @Override
    public Mono<PricingDto> get(List<String> countryCodes) {

        countryCodesQueue.push(countryCodes);

        CompletableFuture<PricingDto> future = CompletableFuture.supplyAsync(() -> {
            PricingDto result = new PricingDto();
            Disposable disposable = pricingDtoFlux.subscribe(result::addAll);
            if (result.keySet().containsAll(countryCodes)) {
                disposable.dispose();
                return result;
            }

            // todo:
            //  this will happen for sure.
            //  somehow fail (timeout?)(error?)
            return null;
        });

        return Mono.fromFuture(future);
    }
}
