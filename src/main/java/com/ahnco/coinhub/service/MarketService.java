package com.ahnco.coinhub.service;

import java.util.List;

public interface MarketService {
    public double getCoinCurrentPrice(String coin);

    List<String> getCoins();
}
