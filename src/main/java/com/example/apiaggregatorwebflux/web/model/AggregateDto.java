package com.example.apiaggregatorwebflux.web.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AggregateDto {

    ShipmentDto shipments;
    TrackingDto track;
    PricingDto pricing;
}
