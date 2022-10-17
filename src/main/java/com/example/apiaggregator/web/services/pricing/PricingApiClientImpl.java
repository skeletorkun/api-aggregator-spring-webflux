package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.configuration.JmsConfig;
import com.example.apiaggregator.web.model.PricingDto;
import com.example.apiaggregator.web.model.events.LimitReachedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static com.example.apiaggregator.configuration.JmsConfig.PRICING_API_RESULT_QUEUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class PricingApiClientImpl implements PricingApiClient {

    private static final String BASE_URL = "http://localhost:8080/pricing";

    private final RestTemplate restTemplate;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.PRICING_API_QUEUE)
    @Override
    public void get(LimitReachedEvent event) {

        log.info("Get pricing {}", event);
        Instant start = Instant.now();

        // !! blocking call, do it async?
        PricingDto result = this.restTemplate
                .getForObject(BASE_URL + "?q=" + String.join(",", event.getParams()), PricingDto.class);

        log.info("Get pricing finished in {} ms with values: {}", Instant.now().toEpochMilli() - start.toEpochMilli(), result);

        jmsTemplate.convertAndSend(PRICING_API_RESULT_QUEUE, result);
    }
}
