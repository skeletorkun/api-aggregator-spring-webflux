package com.example.apiaggregatorwebflux.web.model;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class ShipmentDto extends HashMap<String, List<Product>> {
}
