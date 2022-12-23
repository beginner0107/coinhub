package com.ahnco.coinhub.service.bithumb;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.feign.BithumbFeignClient;
import com.ahnco.coinhub.feign.response.BithumbResponse;
import com.ahnco.coinhub.model.BithumbAssetEachStatus;
import com.ahnco.coinhub.model.BithumbCoinPrice;
import com.ahnco.coinhub.service.BithumbMarketService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

//@Disabled
@EnableFeignClients
@SpringBootTest
class BithumbMarketServiceIntegrationTest {

    @Autowired
    private BithumbMarketService bithumbMarketService;

    @Test
    void calculateFeeTest() throws IOException {
        Map<String, Double> result = bithumbMarketService.calculateFee();
        assertThat(result.isEmpty()).isFalse();
    }
}