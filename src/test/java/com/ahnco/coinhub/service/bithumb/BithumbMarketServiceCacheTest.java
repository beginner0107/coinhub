package com.ahnco.coinhub.service.bithumb;

import com.ahnco.coinhub.constant.CacheConstants;
import com.ahnco.coinhub.feign.BithumbFeignClient;
import com.ahnco.coinhub.service.BithumbMarketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;

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

    @Value("${cache.ttl.default}")
    private String defaultTtl;

    @Value("${cache.ttl.withdrawalFee}")
    private String withdrawalFeeTtl;

    @BeforeEach
    void setUp(){
        cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).clear();
        cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).clear();
        cacheManager.getCache(CacheConstants.BITHUMB_ORDER_BOOK).clear();
        cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).clear();
    }

    @Test
    void getCoinPriceTest() throws Exception{
        String parameter = "BTC";
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).get(parameter));

        bithumbFeignClient.getCoinPrice(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).get(parameter));
    }

    @Test
    void getCoinPriceTimeTest() throws Exception{
        String parameter = "BTC";
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).get(parameter));

        bithumbFeignClient.getCoinPrice(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).get(parameter));

        Thread.sleep(Long.parseLong(defaultTtl));

        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_COIN_PRICE).get(parameter));
    }

    @Test
    void getAssetsStatusTest(){
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).get(SimpleKey.EMPTY));

        bithumbFeignClient.getAssetStatus();

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).get(SimpleKey.EMPTY));
    }

    @Test
    void getAssetStatusCacheTimeTest() throws Exception{
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).get(SimpleKey.EMPTY));

        bithumbFeignClient.getAssetStatus();

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).get(SimpleKey.EMPTY));


        Thread.sleep(Long.parseLong(withdrawalFeeTtl));
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_ASSET_STATUS).get(SimpleKey.EMPTY));
    }

    @Test
    void getOrderBookTest(){
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_ORDER_BOOK).get(SimpleKey.EMPTY));

        bithumbFeignClient.getOrderBook();

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_ORDER_BOOK).get(SimpleKey.EMPTY));
    }

    @Test
    void calculateFeeTest() throws IOException {
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        bithumbMarketService.calculateFee();

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));
    }

    @Test
    void calculateFeeTimeTest() throws Exception{
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        bithumbMarketService.calculateFee();

        assertNotNull(cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        Thread.sleep(Long.parseLong(withdrawalFeeTtl));
        assertNull(cacheManager.getCache(CacheConstants.BITHUMB_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));
    }
}