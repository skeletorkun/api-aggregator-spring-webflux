package com.example.apiaggregator.web.services;

import com.example.apiaggregator.web.model.AggregateDto;
import com.example.apiaggregator.web.model.PricingDto;
import com.example.apiaggregator.web.model.ShipmentDto;
import com.example.apiaggregator.web.model.TrackingDto;
import com.example.apiaggregator.web.services.pricing.PricingService;
import com.example.apiaggregator.web.services.shipment.ShipmentService;
import com.example.apiaggregator.web.services.tracking.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AggregationServiceImpl implements AggregationService {

    private final ShipmentService shipmentService;
    private final TrackingService trackingService;
    private final PricingService pricingService;

    @Override
    public Mono<AggregateDto> get(List<String> pricing, List<String> track, List<String> shipments) {
        Mono<ShipmentDto> shipmentMono = this.shipmentService.get(shipments);
        Mono<TrackingDto> trackingMono = this.trackingService.get(track);
        Mono<PricingDto> pricingMono = this.pricingService.get(pricing);
        return Mono.zip(shipmentMono, trackingMono, pricingMono).map(this::buildAggregate);
    }

    private AggregateDto buildAggregate(Tuple3<ShipmentDto, TrackingDto, PricingDto> tuple) {

        var result = AggregateDto.builder()
                .shipments(tuple.getT1())
                .track(tuple.getT2())
                .pricing(tuple.getT3())
                .build();
        log.info("Aggregate result {}", result);
        return result;
    }
}
