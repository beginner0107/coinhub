package com.ahnco.coinhub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class CoinSellDTO {
    private Map<String, Double> amounts;
    private Map<String, Map<Double, Double>> orderBooks;
}
