package com.example.apiaggregator.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.StringJoiner;

@AllArgsConstructor
@Data
public class PricingDto extends HashMap<String, Double> {
    private final Boolean hasError;

    public PricingDto() {
        hasError = false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PricingDto.class.getSimpleName() + "[", "]")
                .add("hasError=" + hasError)
                .add("elements=" + super.toString())
                .toString();
    }
}
