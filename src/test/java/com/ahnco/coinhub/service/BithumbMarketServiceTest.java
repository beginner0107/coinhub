package com.ahnco.coinhub.service;

import com.ahnco.coinhub.feign.BithumbFeignClient;
import com.ahnco.coinhub.feign.response.BithumbResponse;
import com.ahnco.coinhub.model.BithumbAssetEachStatus;
import com.ahnco.coinhub.model.BithumbCoinPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BithumbMarketServiceTest {

    @Mock
    private BithumbFeignClient bithumbFeignClient;

    @InjectMocks
    private BithumbMarketService bithumbMarketService;

    @Test
    void getCoinCurrentPriceTest(){
        // Given
        String mockCoin = "testCoin";
        String mockPrice = "123.456";
        BithumbCoinPrice mockBithumbCoinPrice = new BithumbCoinPrice();
        mockBithumbCoinPrice.setClosing_price(mockPrice);

        // When
        when(bithumbFeignClient.getCoinPrice(mockCoin.toUpperCase()+"_KRW"))
                .thenReturn(mockBithumbCoinPrice(mockPrice));

        // Then
        assertThat(bithumbMarketService.getCoinCurrentPrice(mockCoin))
                .isEqualTo(Double.parseDouble(mockPrice));
    }

    @Test
    void getCoins(){
        // Given
        String mockCoin1 = "btc";
        String mockCoin2 = "ltc";
        String mockCoin3 = "atm";

        // When
        when(bithumbFeignClient.getAssetStatus())
                .thenReturn(mockBithumbAssetStatus(mockCoin1, mockCoin2, mockCoin3));

        // Then
        List<String> result = bithumbMarketService.getCoins();
        assertThat(result.contains(mockCoin1.toUpperCase())).isTrue();
        assertThat(result.contains(mockCoin2.toUpperCase())).isTrue();
        assertThat(result.contains(mockCoin3.toUpperCase())).isTrue();
        assertThat(result.size()).isEqualTo(3);
    }

    private BithumbResponse<BithumbCoinPrice> mockBithumbCoinPrice(String price) {
        BithumbResponse response = new BithumbResponse();
        BithumbCoinPrice data = new BithumbCoinPrice();
        data.setClosing_price(price);
        response.setData(data);
        return response;
    }

    private BithumbResponse<Map<String, BithumbAssetEachStatus>> mockBithumbAssetStatus(String coin1, String coin2, String coin3) {
        BithumbResponse response = new BithumbResponse();
        Map<String, BithumbAssetEachStatus> data = new HashMap<>();
        data.put(coin1, new BithumbAssetEachStatus(1, 1));
        data.put(coin2, new BithumbAssetEachStatus(1, 1));
        data.put(coin3, new BithumbAssetEachStatus(1, 1));
        response.setData(data);
        return response;
    }
}