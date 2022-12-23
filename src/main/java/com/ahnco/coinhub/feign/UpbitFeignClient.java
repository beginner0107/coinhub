package com.ahnco.coinhub.feign;

import com.ahnco.coinhub.constant.CacheConstants;
import com.ahnco.coinhub.model.UpbitCoinPrice;
import com.ahnco.coinhub.model.UpbitMarketCode;
import com.ahnco.coinhub.model.UpbitOrderBooks;
import lombok.EqualsAndHashCode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "upbit", url = "https://api.upbit.com/v1")
public interface UpbitFeignClient {

    @Cacheable(CacheConstants.UPBIT_COIN_PRICE)
    @GetMapping("/ticker")
    List<UpbitCoinPrice> getCoinPrice(@RequestParam("markets") String coin);

    @Cacheable(CacheConstants.UPBIT_MARKET_CODE)
    @GetMapping("/market/all")
    List<UpbitMarketCode> getMarketCode();

    @Cacheable(CacheConstants.UPBIT_ORDER_BOOKS)
    @GetMapping("/orderbook")
    List<UpbitOrderBooks> getOrderBooks(@RequestParam("markets") List<String> markets);
}
