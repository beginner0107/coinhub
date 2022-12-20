package com.ahnco.coinhub.service;

import com.ahnco.coinhub.feign.UpbitFeignClient;
import com.ahnco.coinhub.model.UpbitCoinPrice;
import com.ahnco.coinhub.model.UpbitMarketCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpbitMarketServiceTest {

    @Mock
    private UpbitFeignClient upbitFeignClient;

    @InjectMocks
    private UpbitMarketService upbitMarketService;

    @Test
    void getCoinCurrentPriceTest(){
        // Given
        String mockCoin = "testCoin";
        double mockPrice = 123.456;
        UpbitCoinPrice mockUpbitCoinPrice = new UpbitCoinPrice();
        mockUpbitCoinPrice.setTrade_price(mockPrice);

        // When
        when(upbitFeignClient.getCoinPrice("KRW-TESTCOIN")).thenReturn(List.of(mockUpbitCoinPrice));

        // Then
        assertThat(upbitMarketService.getCoinCurrentPrice(mockCoin)).isEqualTo(mockPrice);
    }

    @Test
    void getCoins(){
        // Given
        String mockCoin1 = "coin1";
        String mockCoin2 = "coin2";
        String mockCoin3 = "coin3";
        UpbitMarketCode mockUpbitCoin1 = mockUpbitMarketCode(mockCoin1);
        UpbitMarketCode mockUpbitCoin2 = mockUpbitMarketCode(mockCoin2);
        UpbitMarketCode mockUpbitCoin3 = mockUpbitMarketCode(mockCoin3);

        // When
        when(upbitFeignClient.getMarketCode()).thenReturn(List.of(mockUpbitCoin1, mockUpbitCoin2, mockUpbitCoin3));

        // Then
        List<String> result = upbitMarketService.getCoins();
        assertThat(result.contains(mockCoin1.toUpperCase())).isTrue();
        assertThat(result.contains(mockCoin2.toUpperCase())).isTrue();
        assertThat(result.contains(mockCoin3.toUpperCase())).isTrue();
        assertThat(result.size()).isEqualTo(3);
    }

    private UpbitMarketCode mockUpbitMarketCode(String coin){
        UpbitMarketCode result = new UpbitMarketCode();
        result.setMarket("KRW-" + coin);
        return result;
    }
}