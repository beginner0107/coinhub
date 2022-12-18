package com.ahnco.coinhub.controller;

import com.ahnco.coinhub.service.TransferCalculateService;
import com.ahnco.coinhub.view.TransferCalculateResponseView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class TransferCalculateController {

    @GetMapping("/transfer-calculate")
    public TransferCalculateResponseView getPrice(
            @RequestParam String fromMarket,
            @RequestParam String toMarket,
            @RequestParam double amount
    ) {
        return new TransferCalculateResponseView(
          "BTC",
          100000,
          Map.of(123.123D, 123.123D),
          Map.of(123.123D, 123.123D)
        );
    }
}
