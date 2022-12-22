package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;
import com.ahnco.coinhub.feign.UpbitFeeFeignClient;
import com.ahnco.coinhub.feign.UpbitFeignClient;
import com.ahnco.coinhub.model.UpbitEachWithdrawalFee;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpbitMarketService implements MarketService{

    private final UpbitFeignClient upbitFeignClient;
    private final UpbitFeeFeignClient upbitFeeFeignClient;

    @Override
    public double getCoinCurrentPrice(String coin) {
        // coin : 대문자, 소문자
        return upbitFeignClient.getCoinPrice("KRW-" + coin.toUpperCase())
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

    @Override
    public CoinBuyDTO calculateBuy(List<String> commonCoins, double amount) {
        return null;
    }

    @Override
    public CoinSellDTO calculateSell(CoinBuyDTO buyDTO) {
        return null;
    }

    @Override
    public Map<String, Double> calculateFee() throws JsonProcessingException {
        return upbitFeeFeignClient.getWithdrawalFee().getData()
                .stream()
                .collect(Collectors.toMap(
                        UpbitEachWithdrawalFee::getCurrency,
                        UpbitEachWithdrawalFee::getWithdrawFee
                ));
    }
}
