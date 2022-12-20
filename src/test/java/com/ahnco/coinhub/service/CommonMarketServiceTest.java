package com.ahnco.coinhub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommonMarketServiceTest {

    @Mock
    private BithumbMarketService bithumbMarketService;
    @Mock
    private UpbitMarketService upbitMarketService;

    private CommonMarketService commonMarketService;


    @BeforeEach
    void setUp(){
         commonMarketService = new CommonMarketService(
                Map.of("bithumbMarketService", bithumbMarketService,
                        "upbitMarketService", upbitMarketService)
        );
    }

    @Test
    void getPriceTest(){
        // Given
        double testAmount = 123.456;
        String testCoin = "testCoin";
        when(bithumbMarketService.getCoinCurrentPrice(testCoin)).thenReturn(testAmount);
        when(upbitMarketService.getCoinCurrentPrice(testCoin)).thenReturn(testAmount);


        // When & Then
        assertThat(commonMarketService.getPrice("bithumb", testCoin)).isEqualTo(testAmount);
        assertThat(commonMarketService.getPrice("upbit", testCoin)).isEqualTo(testAmount);
    }

    @Test
    void getMarketServiceTest(){
        // Given
        Map<String, MarketService> marketServices = new HashMap<>();
        marketServices.put("bithumbMarketService", bithumbMarketService);
        marketServices.put("upbitMarketService", upbitMarketService);

        // When & Then
        assertThat(CommonMarketService.getMarketService(marketServices, "bithumb")).isEqualTo(bithumbMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "Bithumb")).isEqualTo(bithumbMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "BITHUMB")).isEqualTo(bithumbMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "BITHUmb")).isEqualTo(bithumbMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "upbit")).isEqualTo(upbitMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "Upbit")).isEqualTo(upbitMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "UPBIT")).isEqualTo(upbitMarketService);
        assertThat(CommonMarketService.getMarketService(marketServices, "UpBit")).isEqualTo(upbitMarketService);
    }

    @Test
    void getCommonCoin(){
        // Given
        Map<String, MarketService> marketServices = new HashMap<>();
        marketServices.put("bithumbMarketService", bithumbMarketService);
        marketServices.put("upbitMarketService", upbitMarketService);

        String fromMarket = "bithumb";
        String toMarket = "upbit";

        MarketService fromMarketService = CommonMarketService.getMarketService(marketServices, fromMarket);
        MarketService toMarketService = CommonMarketService.getMarketService(marketServices, toMarket);

        when(fromMarketService.getCoins()).thenReturn(List.of("BTC", "ETD", "LTC"));
        when(toMarketService.getCoins()).thenReturn(List.of("BTC", "ETD", "BBC"));

        // When & Then
        assertThat(commonMarketService.getCommonCoin(fromMarket, toMarket)).isEqualTo(List.of("BTC", "ETD"));
    }
}