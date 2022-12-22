package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;
import com.ahnco.coinhub.feign.BithumbFeignClient;
import com.ahnco.coinhub.feign.response.BithumbResponse;
import com.ahnco.coinhub.model.BithumbAssetEachStatus;
import com.ahnco.coinhub.model.BithumbCoinPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    @Test
    void calculateBuyTest(){
        // Given
        List<String> commonCoin = List.of("A", "B");

        BithumbResponse<Map<String, Object>> mockOrderBook = mockBithumbOrderBook();
        when(bithumbFeignClient.getOrderBook()).thenReturn(mockOrderBook);

        // When
        CoinBuyDTO result = bithumbMarketService.calculateBuy(commonCoin, 5);

        // Then
        assertThat(result.getAmounts().get("A")).isEqualTo(1 + 1 + 0.5);
        assertThat(result.getOrderBooks().get("A").get(1D)).isEqualTo(1);
        assertThat(result.getOrderBooks().get("A").get(2D)).isEqualTo(1.0);
        assertThat(result.getOrderBooks().get("A").get(4D)).isEqualTo(0.5);

        assertThat(result.getAmounts().get("B")).isEqualTo(2 + 1.5);
        assertThat(result.getOrderBooks().get("B").get(1D)).isEqualTo(2.0);
        assertThat(result.getOrderBooks().get("B").get(2D)).isEqualTo(1.5);

        assertThat(result.getAmounts().get("C")).isEqualTo(3 + 1.0);
        assertThat(result.getOrderBooks().get("C").get(1D)).isEqualTo(3.0);
        assertThat(result.getOrderBooks().get("C").get(2D)).isEqualTo(1.0);
    }

    @Test
    void calculateSellTest() {
        // given
        // 특정 코인을 얼마의 수량으로 매수했는지
        Map<String, Double> amounts = Map.of("A", 2.5, "B", 3D, "C", 123D);
        // 얼마에 매수했는지 목록을 받아오는 것을 mockBithumbOrderBook()을 이용해 처리
        BithumbResponse<Map<String, Object>> mockOrderBook = mockBithumbOrderBook();
        when(bithumbFeignClient.getOrderBook()).thenReturn(mockOrderBook);

        // when
        CoinSellDTO result = bithumbMarketService.calculateSell(new CoinBuyDTO(amounts, null));

        // then
        // 4원에 * 1개를 팔고 + 2원에 * 2개를 팔고 + 1원에 0.5개를 판다.
        assertThat(result.getAmounts().get("A")).isEqualTo(4 * 1 + 2 * 1 + 1 * 0.5);
        assertThat(result.getOrderBooks().get("A").get(4D)).isEqualTo(1);
        assertThat(result.getOrderBooks().get("A").get(2D)).isEqualTo(1);
        assertThat(result.getOrderBooks().get("A").get(1D)).isEqualTo(0.5);

        assertThat(result.getAmounts().get("B")).isEqualTo(4 * 2 + 2 * 1);
        assertThat(result.getOrderBooks().get("B").get(4D)).isEqualTo(2);
        assertThat(result.getOrderBooks().get("B").get(2D)).isEqualTo(1);

        assertThat(result.getAmounts().get("C")).isNull();
        assertThat(result.getOrderBooks().get("C")).isNull();
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

    private BithumbResponse<Map<String, Object>> mockBithumbOrderBook() {
        BithumbResponse<Map<String, Object>> result = new BithumbResponse<>();
        result.setData(
                Map.of(
                        "A", Map.of(
                                "bids", new ArrayList<>(List.of( // wanna Buy
                                        Map.of("price", "1","quantity","1"),
                                        Map.of("price", "2","quantity","1"),
                                        Map.of("price", "4","quantity","1")
                                )),
                                "asks", new ArrayList<>(List.of( // wanna Sell
                                        Map.of("price", "4","quantity","1"), // 2
                                        Map.of("price", "2","quantity","1"), // 2
                                        Map.of("price", "1","quantity","1") // 1
                                ))
                        ),
                        "B", Map.of(
                                "bids", new ArrayList<>(List.of( // wanna Buy
                                        Map.of("price", "1","quantity","2"),
                                        Map.of("price", "2","quantity","2"),
                                        Map.of("price", "4","quantity","2")
                                )),
                                "asks", new ArrayList<>(List.of( // wanna Sell
                                        Map.of("price", "4","quantity","2"),
                                        Map.of("price", "2","quantity","2"), // 1.5
                                        Map.of("price", "1","quantity","2") // 2
                                ))
                        ),
                        "C", Map.of(
                                "bids", new ArrayList<>(List.of( // wanna Buy
                                        Map.of("price", "1","quantity","3"),
                                        Map.of("price", "2","quantity","3"),
                                        Map.of("price", "4","quantity","3")
                                )),
                                "asks", new ArrayList<>(List.of( // wanna Sell
                                        Map.of("price", "4","quantity","3"),
                                        Map.of("price", "2","quantity","3"), // 1
                                        Map.of("price", "1","quantity","3") // 3
                                ))
                        )
                )
        );

        return result;
    }
}