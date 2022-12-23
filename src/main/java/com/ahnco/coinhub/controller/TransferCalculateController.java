package com.ahnco.coinhub.controller;

import com.ahnco.coinhub.dto.TransferCalculateDTO;
import com.ahnco.coinhub.service.TransferCalculateService;
import com.ahnco.coinhub.view.TransferCalculateResponseView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@Slf4j
public class TransferCalculateController {

    private final TransferCalculateService transferCalculateService;

    @GetMapping("/transfer-calculate")
    public List<TransferCalculateResponseView> getPrice(
            @RequestParam String fromMarket,
            @RequestParam String toMarket,
            @RequestParam double amount
    ) throws Exception {
        return transferCalculateService.calculate(fromMarket, toMarket, amount)
                .stream()
                .map(k -> TransferCalculateResponseView.of(k, amount)).toList();
    }
}
