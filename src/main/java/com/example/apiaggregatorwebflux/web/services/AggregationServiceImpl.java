package com.example.apiaggregatorwebflux.web.services;

import com.example.apiaggregatorwebflux.web.model.AggregateDto;
import com.example.apiaggregatorwebflux.web.model.PricingDto;
import com.example.apiaggregatorwebflux.web.model.ShipmentDto;
import com.example.apiaggregatorwebflux.web.model.TrackingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;

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
        return AggregateDto.builder()
                .shipments(tuple.getT1())
                .track(tuple.getT2())
                .pricing(tuple.getT3())
                .build();
    }
}
