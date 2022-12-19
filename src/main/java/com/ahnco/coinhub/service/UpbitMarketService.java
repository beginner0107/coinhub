package com.ahnco.coinhub.service;

import com.ahnco.coinhub.feign.UpbitFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpbitMarketService implements MarketService{

    private final UpbitFeignClient upbitFeignClient;

    @Override
    public double getCoinCurrentPrice(String coin) {
        // coin : 대문자, 소문자
        return upbitFeignClient.getCoinPrice("KRW-" + coin.toLowerCase())
                .get(0)
                .getTrade_price();
    }

    @Override
    public List<String> getCoins() {
        // API 활용해서 가져와야지
        List<String> result = new ArrayList<>();
        upbitFeignClient.getMarketCode().forEach(k -> {
            if(k.getMarket().startsWith("KRW")){
                result.add(k.getMarket().substring(4).toUpperCase());
            }
        });
        return result;
    }
}
