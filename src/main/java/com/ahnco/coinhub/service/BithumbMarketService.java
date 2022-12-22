package com.ahnco.coinhub.service;

import com.ahnco.coinhub.dto.CoinBuyDTO;
import com.ahnco.coinhub.dto.CoinSellDTO;
import com.ahnco.coinhub.feign.BithumbFeignClient;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BithumbMarketService implements MarketService {

    private final BithumbFeignClient bithumbFeignClient;

    @Value("${feeUrl.bithumb}")
    private String feeUrl;

    @Override
    public double getCoinCurrentPrice(String coin) {
        return Double.parseDouble(
                bithumbFeignClient.getCoinPrice(coin.toUpperCase() + "_KRW")
                        .getData()
                        .getClosing_price());
    }

    @Override
    public List<String> getCoins() {
        List<String> result = new ArrayList<>();
        bithumbFeignClient.getAssetStatus().getData().forEach((k, v) -> {
            if(v.getDeposit_status() == 1 && v.getWithdrawal_status() == 1){
                result.add(k.toUpperCase());

            }
        });
        return result;
    }

    @Override
    public CoinBuyDTO calculateBuy(List<String> commonCoins, double amount) {
        Map<String, Double> amounts = new HashMap<>();
        Map<String, Map<Double, Double>> orderBooks = new HashMap<>();

        // Feign으로 orderbook(호가 창) 가져오기
        Map<String, Object> bithumbResponse = bithumbFeignClient.getOrderBook().getData();

        // orderbook for 돌면
        bithumbResponse.forEach((k, v) -> {
            if(!(k.equalsIgnoreCase("timestamp") || k.equalsIgnoreCase("payment_currency"))){
                double availableCurrency = amount;
                double availableCoin = 0;
                String coin = k;
                Map<Double, Double> eachOrderBook = new HashMap<>();
                List<Map<String, String>> wannaSell =
                        (List<Map<String, String>>) ((Map<String, Object>) v).get("asks");

                for(int i = 0; i < wannaSell.size(); i ++){
                    Double price = Double.valueOf(wannaSell.get(i).get("price"));
                    Double quantity = Double.valueOf(wannaSell.get(i).get("quantity"));
                    double eachTotalPrice = price * quantity;
                    double buyableCoinAmount = availableCurrency / price;

                    // 해당 호가창의 총 가격보다 큰지 작은지 비교
                    // amount <= X : 현재 호가창에서 내가 살수 있는만큼 추가하고 종료
                    if(availableCurrency <= eachTotalPrice){
                        availableCoin += buyableCoinAmount;
                        // 살수 있는 호가창에 추가
                        eachOrderBook.put(price, buyableCoinAmount);
                        availableCurrency = 0;
                        break;
                    }else{ // amount > X : 현재 호가창보다 내가 돈이 더 많은 경우, 다음 스템으로 넘어가고
                        availableCoin += quantity;
                        eachOrderBook.put(price, quantity);
                        availableCurrency -= eachTotalPrice;
                    }
                }
                if(availableCurrency == 0) {
                    amounts.put(coin, availableCoin); // 코인, 살 수 있는 코인의 개수
                    orderBooks.put(coin, eachOrderBook); // 총 호가를 넣어줘야 한다
                }
            }
        });
        return new CoinBuyDTO(amounts, orderBooks);
    }

    @Override
    public CoinSellDTO calculateSell(CoinBuyDTO buyDTO) {
        return null;
    }

    @Override
    public Map<String, Double> calculateFee() throws IOException {
        Map<String, Double> result = new HashMap<>();
        Document doc = Jsoup.connect(feeUrl).timeout(10000).get();
        Elements elements = doc.select("table.fee_in_out tbody tr");

        for(int i = 1; i < elements.size(); i ++){
            String coinHtml = elements.get(i).select("td.money_type.tx_c").html().trim();
            coinHtml = coinHtml.substring(coinHtml.indexOf("(") + 1, coinHtml.indexOf(")"));

            String coinFeeHtml = elements.get(i).select("div.right.out_fee").html().trim();
            if(coinFeeHtml.length() == 0) coinFeeHtml = "0";
            result.put(coinHtml, Double.parseDouble(coinFeeHtml));
        }
        return result;
    }
}
