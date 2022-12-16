package com.ahnco.coinhub.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonMarketService {

    private final Map<String, MarketService> marketServices;

    public CommonMarketService(Map<String, MarketService> marketServices) {
        this.marketServices = marketServices;
    }

    public double getPrice(String market, String coin) {
        MarketService marketService = null;
        for(String key : marketServices.keySet()){
            if(key.substring(0, market.length()).equals(market.toLowerCase())){
                marketService = marketServices.get(key);
                break;
            }
        }
         return marketService.getCoinCurrentPrice(coin);
    }
}
