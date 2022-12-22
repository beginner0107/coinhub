package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MarketService {
    public double getCoinCurrentPrice(String coin);

    List<String> getCoins();

    CoinBuyDTO calculateBuy(List<String> commonCoins, double amount);

    CoinSellDTO calculateSell(CoinBuyDTO buyDTO);

    Map<String /* Coin Name */, Double/* Withdrawal Fee */> calculateFee() throws IOException;
}
