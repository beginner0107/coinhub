package com.ahnco.coinhub.service.upbit;

import com.ahnco.coinhub.constant.CacheConstants;
import com.ahnco.coinhub.feign.UpbitFeeFeignClient;
import com.ahnco.coinhub.feign.UpbitFeignClient;
import com.ahnco.coinhub.service.UpbitMarketService;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

//@Disabled
@EnableCaching
@EnableFeignClients
@SpringBootTest
class UpbitMarketServiceCacheTest {
    @Autowired
    private UpbitFeeFeignClient upbitFeeFeignClient;
    @Autowired
    private UpbitFeignClient upbitFeignClient;
    @Autowired
    private CacheManager cacheManager;

    @Value("${cache.ttl.default}")
    private String defaultTtl;

    @Value("${cache.ttl.withdrawalFee}")
    private String withdrawalFeeTtl;

    @BeforeEach
    void setUp() {
        cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).clear();
        cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).clear();
        cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).clear();
        cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).clear();
    }

    @Test
    void getWithdrawalFeeCacheTest() {
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        upbitFeeFeignClient.getWithdrawalFee();

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));
    }

    @Test
    void getWithdrawalFeeCacheTimeTest() throws Exception{
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        upbitFeeFeignClient.getWithdrawalFee();

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));

        Thread.sleep(Long.parseLong(withdrawalFeeTtl));

        assertNull(cacheManager.getCache(CacheConstants.UPBIT_WITHDRAWAL_FEE).get(SimpleKey.EMPTY));
    }

    @Test
    void getCoinPriceCacheTest() {
        String parameter = "KRW-BTC";
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).get(parameter));

        upbitFeignClient.getCoinPrice(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).get(parameter));
    }

    @Test
    void getCoinPriceCacheTimeTest() throws Exception{
        String parameter = "KRW-BTC";
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).get(parameter));

        upbitFeignClient.getCoinPrice(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).get(parameter));

        Thread.sleep(Long.parseLong(defaultTtl));

        assertNull(cacheManager.getCache(CacheConstants.UPBIT_COIN_PRICE).get(parameter));
    }

    @Test
    void getMarketCodeCacheTest() {
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).get(SimpleKey.EMPTY));

        upbitFeignClient.getMarketCode();

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).get(SimpleKey.EMPTY));
    }

    @Test
    void getMarketCodeCacheTimeTest() throws Exception{
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).get(SimpleKey.EMPTY));

        upbitFeignClient.getMarketCode();

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).get(SimpleKey.EMPTY));

        Thread.sleep(Long.parseLong(withdrawalFeeTtl));

        assertNull(cacheManager.getCache(CacheConstants.UPBIT_MARKET_CODE).get(SimpleKey.EMPTY));
    }

    @Test
    void getOrderBooksCacheTest() {
        List<String> parameter = List.of("KRW-BTC");
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).get(parameter));

        upbitFeignClient.getOrderBooks(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).get(parameter));
    }

    @Test
    void getOrderBooksCacheTimeTest() throws Exception{
        List<String> parameter = List.of("KRW-BTC");
        assertNull(cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).get(parameter));

        upbitFeignClient.getOrderBooks(parameter);

        assertNotNull(cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).get(parameter));

        Thread.sleep(Long.parseLong(defaultTtl));

        assertNull(cacheManager.getCache(CacheConstants.UPBIT_ORDER_BOOKS).get(parameter));
    }
}