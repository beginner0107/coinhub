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

    private TransferCalculateService transferCalculateService;

    @GetMapping("/transfer-calculate")
    public TransferCalculateResponseView getPrice(
            @RequestParam String fromMarket,
            @RequestParam String toMarket,
            @RequestParam double amount
    ) {
        return new TransferCalculateResponseView(
                "BTC", 123.45,
                Map.of(123D, 456D),
                Map.of(123D, 456D)
        );
    }
}
