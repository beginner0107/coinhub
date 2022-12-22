package com.ahnco.coinhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.SortedMap;

@AllArgsConstructor
@Getter
public class CoinSellDTO {
    private Map<String, Double> amounts;
    private Map<String, SortedMap<Double, Double>> orderBooks;
}
