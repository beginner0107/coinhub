package com.ahnco.coinhub.service.bithumb;

import com.ahnco.coinhub.feign.BithumbFeignClient;
import com.ahnco.coinhub.service.BithumbMarketService;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled
@EnableCaching
@EnableFeignClients
@SpringBootTest
class BithumbMarketServiceCacheTest {

    @Autowired
    private BithumbFeignClient bithumbFeignClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private BithumbMarketService bithumbMarketService;

    @Test
    void getCoinPriceTest() throws Exception{
        String parameter = "BTC";
        assertNull(cacheManager.getCache("BITHUMB_COIN_PRICE").get(parameter));

        bithumbFeignClient.getCoinPrice(parameter);

        assertNotNull(cacheManager.getCache("BITHUMB_COIN_PRICE").get(parameter));
    }

    @Test
    void getAssetsStatusTest(){
        assertNull(cacheManager.getCache("BITHUMB_ASSET_STATUS").get(SimpleKey.EMPTY));

        bithumbFeignClient.getAssetStatus();

        assertNotNull(cacheManager.getCache("BITHUMB_ASSET_STATUS").get(SimpleKey.EMPTY));
    }

    @Test
    void getOrderBookTest(){
        assertNull(cacheManager.getCache("BITHUMB_ORDER_BOOK").get(SimpleKey.EMPTY));

        bithumbFeignClient.getOrderBook();

        assertNotNull(cacheManager.getCache("BITHUMB_ORDER_BOOK").get(SimpleKey.EMPTY));
    }

    @Test
    void calculateFeeTest() throws IOException {
        assertNull(cacheManager.getCache("BITHUMB_CALCULATE_FEE").get(SimpleKey.EMPTY));

        bithumbMarketService.calculateFee();

        assertNotNull(cacheManager.getCache("BITHUMB_CALCULATE_FEE").get(SimpleKey.EMPTY));
    }
}