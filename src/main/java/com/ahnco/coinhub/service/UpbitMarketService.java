package com.ahnco.coinhub.service;

import com.ahnco.coinhub.feign.UpbitFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
