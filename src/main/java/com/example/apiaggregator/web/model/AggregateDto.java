package com.example.apiaggregator.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class AggregateDto {

    ShipmentDto shipments;
    TrackingDto track;
    PricingDto pricing;
}
