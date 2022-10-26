package com.example.apiaggregator.web.services.tracking;

import com.example.apiaggregator.web.model.TrackingDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrackingService {
    Mono<TrackingDto> get(List<String> orderIds);

}
