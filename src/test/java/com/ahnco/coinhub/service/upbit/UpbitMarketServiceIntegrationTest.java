package com.ahnco.coinhub.service.upbit;

import com.ahnco.coinhub.service.UpbitMarketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@Disabled
@EnableFeignClients
@SpringBootTest
class UpbitMarketServiceIntegrationTest {

    @Autowired
    private UpbitMarketService upbitMarketService;

    @Test
    void calculateFeeTest() throws IOException {
        Map<String, Double> result = upbitMarketService.calculateFee();
        assertThat(result.isEmpty()).isFalse();
    }
}