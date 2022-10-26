package com.example.apiaggregator.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import java.util.HashMap;

@AllArgsConstructor
@Data
public class PricingDto extends HashMap<String, Double> {
    private final Boolean hasError;

    public PricingDto() {
        hasError = false;
    }
}
