package com.ahnco.coinhub.view;

import com.ahnco.coinhub.dto.TransferCalculateDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class TransferCalculateResponseView {
    private String coin;
    private double buyAmount;
    private double fee;
    private double sellAmount;
    private Map<Double, Double> buyOrderBook;
    private Map<Double, Double> sellOrderBook;
    private double profit;
    private double profitRatio;

    public static TransferCalculateResponseView of(TransferCalculateDTO dto, double input){
        return new TransferCalculateResponseView(
          dto.getCoin(),
          dto.getBuyAmount(),
          dto.getFee(),
          dto.getSellAmount(),
          dto.getBuyOrderBook(),
          dto.getSellOrderBook(),
    dto.getSellAmount() - input,
 dto.getSellAmount() / input
        );
    }
}
