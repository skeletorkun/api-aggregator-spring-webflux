package com.example.apiaggregator.web.services.pricing;

import com.example.apiaggregator.web.model.events.LimitReachedEvent;

public interface PricingApiClient {
    void get(LimitReachedEvent event);
}
