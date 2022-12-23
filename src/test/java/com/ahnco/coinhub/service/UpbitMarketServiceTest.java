package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;
import com.ahnco.coinhub.feign.UpbitFeignClient;
import com.ahnco.coinhub.model.UpbitCoinPrice;
import com.ahnco.coinhub.model.UpbitMarketCode;
import com.ahnco.coinhub.model.UpbitOrderBooks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void calculateBuyTest() {
        // given
        List<String> commonCoins = List.of("A", "B");
        List<String> krw_commonCoins = List.of("KRW-A", "KRW-B");
        List<UpbitOrderBooks> mockOrderBook = mockUpbitOrderBooks();
        when(upbitFeignClient.getOrderBooks(krw_commonCoins)).thenReturn(mockOrderBook);


        // when
        CoinBuyDTO result = upbitMarketService.calculateBuy(commonCoins, 5);

        // then
        assertEquals(1+1+0.5, result.getAmounts().get("A"));
        assertEquals(1, result.getOrderBooks().get("A").get(1D));
        assertEquals(1, result.getOrderBooks().get("A").get(2D));
        assertEquals(0.5, result.getOrderBooks().get("A").get(4D));

        assertEquals(2+1.5, result.getAmounts().get("B"));
        assertEquals(2, result.getOrderBooks().get("B").get(1D));
        assertEquals(1.5, result.getOrderBooks().get("B").get(2D));
    }

    @Test
    void calculateSellTest() {
        // given
        Map<String, Double> amounts = Map.of("A", 2.5, "B", 3D, "C", 123D);
        List<UpbitOrderBooks> mockOrderBook = mockUpbitOrderBooks();
        List<String> commonCoins = amounts.keySet()
                .stream()
                .map(k -> "KRW-" + k)
                .toList();
        when(upbitFeignClient.getOrderBooks(commonCoins)).thenReturn(mockOrderBook);

        // when
        CoinSellDTO result = upbitMarketService.calculateSell(amounts);

        // then
        assertEquals(4*1 + 2*1 + 1*0.5, result.getAmounts().get("A"));
        assertEquals(1, result.getOrderBooks().get("A").get(4D));
        assertEquals(1, result.getOrderBooks().get("A").get(2D));
        assertEquals(0.5, result.getOrderBooks().get("A").get(1D));

        assertEquals(4*2 + 2*1, result.getAmounts().get("B"));
        assertEquals(2, result.getOrderBooks().get("B").get(4D));
        assertEquals(1, result.getOrderBooks().get("B").get(2D));

        assertNull(result.getAmounts().get("C"));
        assertNull(result.getOrderBooks().get("C"));
    }

    private UpbitMarketCode mockUpbitMarketCode(String coin){
        UpbitMarketCode result = new UpbitMarketCode();
        result.setMarket("KRW-" + coin);
        return result;
    }

    private List<UpbitOrderBooks> mockUpbitOrderBooks() {
        UpbitOrderBooks orderbook1 = new UpbitOrderBooks();
        orderbook1.setMarket("KRW-A");
        orderbook1.setOrderbook_units(new ArrayList<>(List.of(
                new UpbitOrderBooks.UpbitEachOrderBooks(4D, 1D, 1D, 1D),
                new UpbitOrderBooks.UpbitEachOrderBooks(2D, 2D, 1D, 1D),
                new UpbitOrderBooks.UpbitEachOrderBooks(1D, 4D, 1D, 1D)
        )));

        UpbitOrderBooks orderbook2 = new UpbitOrderBooks();
        orderbook2.setMarket("KRW-B");
        orderbook2.setOrderbook_units(new ArrayList<>(List.of(
                new UpbitOrderBooks.UpbitEachOrderBooks(4D, 1D, 2D, 2D),
                new UpbitOrderBooks.UpbitEachOrderBooks(2D, 2D, 2D, 2D),
                new UpbitOrderBooks.UpbitEachOrderBooks(1D, 4D, 2D, 2D)
        )));

        UpbitOrderBooks orderbook3 = new UpbitOrderBooks();
        orderbook3.setMarket("KRW-C");
        orderbook3.setOrderbook_units(new ArrayList<>(List.of(
                new UpbitOrderBooks.UpbitEachOrderBooks(4D, 1D, 3D, 3D),
                new UpbitOrderBooks.UpbitEachOrderBooks(2D, 2D, 3D, 3D),
                new UpbitOrderBooks.UpbitEachOrderBooks(1D, 4D, 3D, 3D)
        )));


        return List.of(orderbook1, orderbook2, orderbook3);
    }
}