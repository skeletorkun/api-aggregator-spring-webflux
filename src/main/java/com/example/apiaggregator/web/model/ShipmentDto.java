package com.example.apiaggregator.web.model;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class ShipmentDto extends HashMap<String, List<Product>> {
}
