package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;
import com.ahnco.coinhub.dto.TransferCalculateDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferCalculateServiceTest {

    @Mock CommonMarketService commonMarketService;
    @Mock Map<String, MarketService> marketServices;
    @Mock MarketService fromMarketService;
    @Mock MarketService toMarketService;
    private TransferCalculateService transferCalculateService;

    @BeforeEach
    void setUp() {
        transferCalculateService = new TransferCalculateService(
                commonMarketService, Map.of("fromMarketService", fromMarketService,
                "toMarketService", toMarketService)
        );
    }

    @Test
    void calculateTest() throws Exception{
        // Given
        String fromMarketName = "from";
        String toMarketName = "to";
        List<String> commonCoins = List.of("A", "B");
        CoinBuyDTO coinBuyDto = mockCoinBuyDTO();
        CoinSellDTO coinSellDto = mockCoinSellDTO();

        Map<String, Double> fromMarketTransferFee = Map.of("A",0.1D, "B", 0.01D);
        when(commonMarketService.getCommonCoin(fromMarketName, toMarketName)).thenReturn(commonCoins);
        when(fromMarketService.calculateBuy(commonCoins, 5D)).thenReturn(coinBuyDto);
        when(fromMarketService.calculateFee()).thenReturn(fromMarketTransferFee);
        when(toMarketService.calculateSell(Map.of("A", 2.4, "B", 3.49))).thenReturn(coinSellDto);

        // When
        List<TransferCalculateDTO> result = transferCalculateService.calculate("from", "to", 5D);

        // Then
        assertThat(result.size()).isEqualTo(commonCoins.size());

        assertThat(result.get(0).getCoin()).isEqualTo("B");
        assertThat(result.get(0).getBuyAmount()).isEqualTo(coinBuyDto.getAmounts().get("B"));
        assertThat(result.get(0).getFee()).isEqualTo(fromMarketTransferFee.get("B"));
        assertThat(result.get(0).getSellAmount()).isEqualTo(coinSellDto.getAmounts().get("B"));
        assertThat(result.get(0).getBuyOrderBook()).isEqualTo(coinBuyDto.getOrderBooks().get("B"));
        assertThat(result.get(0).getSellOrderBook()).isEqualTo(coinSellDto.getOrderBooks().get("B"));

        assertThat(result.get(1).getCoin()).isEqualTo("A");
        assertThat(result.get(1).getBuyAmount()).isEqualTo(coinBuyDto.getAmounts().get("A"));
        assertThat(result.get(1).getFee()).isEqualTo(fromMarketTransferFee.get("A"));
        assertThat(result.get(1).getSellAmount()).isEqualTo(coinSellDto.getAmounts().get("A"));
        assertThat(result.get(1).getBuyOrderBook()).isEqualTo(coinBuyDto.getOrderBooks().get("A"));
        assertThat(result.get(1).getSellOrderBook()).isEqualTo(coinSellDto.getOrderBooks().get("A"));
    }

    private CoinBuyDTO mockCoinBuyDTO() {
        return new CoinBuyDTO(
                Map.of( // A : 1개 + 1개 + 0.5개 == 2.5개 구매 
                        "A", 1 + 1 + 0.5,
                        "B", 2 + 1.5 // B : 2개 + 1.5개 == 3.5개 구매
                ),
                Map.of( // Map<가격, 수량>
                        "A", new TreeMap<>(Map.of(1D, 1D, 2D, 1D, 4D, 0.5)),
                        // A는 1의 가격을 가지는 코인 1D개를 구매
                        // A는 2의 가격을 가지는 코인 1D개를 구매
                        // A는 4의 가격을 가지는 코인 0.5D개를 구매
                        "B", new TreeMap<>(Map.of(1D, 2D, 2D, 1.5))
                )
        );
    }

    private CoinSellDTO mockCoinSellDTO() {
        // A : 1 + 1 + 0.5 - 0.1(수수료) = 2.5
        // B : 2 + 1.5 - 0.1(수수료) = 3.49

        SortedMap<Double, Double> orderBooksForA = new TreeMap<>(Collections.reverseOrder());
        orderBooksForA.put(4D, 1D);
        orderBooksForA.put(2D, 1D);
        orderBooksForA.put(1D, 0.4D);

        SortedMap<Double, Double> orderBooksForB = new TreeMap<>(Collections.reverseOrder());
        orderBooksForB.put(4D, 2D);
        orderBooksForB.put(2D, 1.49);

        return new CoinSellDTO(
                Map.of(
                        "A", 1 * 4 + 1 * 2 + 0.4 * 1,
                        "B", 2 * 4 + 1.49 * 2
                ),
                Map.of(
                        "A", orderBooksForA,
                        "B", orderBooksForB
                )
        );
    }

}