package com.example.apiaggregator.web.model;

import lombok.AllArgsConstructor;

import java.util.HashMap;

@AllArgsConstructor
public class PricingDto extends HashMap<String, Double> {

    public void addAll(PricingDto another){
        this.entrySet().addAll(another.entrySet());
    }
}
