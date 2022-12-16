package com.ahnco.coinhub.service;

import com.ahnco.coinhub.feign.BithumbFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BithumbMarketService implements MarketService {

    private final BithumbFeignClient bithumbFeignClient;

    @Override
    public double getCoinCurrentPrice(String coin) {
        return Double.parseDouble(
                bithumbFeignClient.getCoinPrice(coin.toLowerCase() + "_KRW")
                        .getData()
                        .getClosing_price());
    }
}
