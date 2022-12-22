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
import java.util.*;

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
        Map<String, SortedMap<Double, Double>> orderBooks = new HashMap<>();

        // Feign으로 orderbook(호가 창) 가져오기
        Map<String, Object> bithumbResponse = bithumbFeignClient.getOrderBook().getData();

        // orderbook for 돌면
        bithumbResponse.forEach((k, v) -> { // key: coin, v: object
            if(!(k.equalsIgnoreCase("timestamp") || k.equalsIgnoreCase("payment_currency"))) {
                double availableCurrency = amount;
                double availableCoin = 0;

                String coin = k;
                SortedMap<Double, Double> eachOrderBook = new TreeMap<>();
                List<Map<String, String>> wannaSell =
                        (List<Map<String, String>>) ((Map<String, Object>) v).get("asks");

                wannaSell.sort(Comparator.comparingDouble(k1 -> Double.parseDouble(k1.get("price")))); // 오름차순


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
        Map<String, Double> sellingAmounts = buyDTO.getAmounts(); // 어떤 코인을 얼마큼 샀는지 ?
        Map<String, Double> amounts = new HashMap<>(); // 어떤 코인을 몇 개 팔것인지?
        Map<String, SortedMap<Double, Double>> orderBooks = new HashMap<>(); // 호가 창 만들기 가격 : 코인 개수

        Map<String, Object> bithumbResponse = bithumbFeignClient.getOrderBook().getData(); // 호가 창 가져오기
        bithumbResponse.forEach((k,v) -> { // key: coin, v: object
            if(!(k.equalsIgnoreCase("timestamp") || k.equalsIgnoreCase("payment_currency"))) {
                String coin = k;
                double sellCurrency = 0; // 팔고 난 다음 금액
                Double availableCoin = sellingAmounts.get(coin); // 코인을 key로 넣어서 얼마의 개수의 코인을 샀는지 가져온다.
                if(availableCoin != null) { // 코인을 샀다면..
                    SortedMap<Double, Double> eachOrderBook = new TreeMap<>(Comparator.reverseOrder()); // 얼마에 매도할지 (가격과 수량)
                    List<Map<String, String>> wannaBuy = (List<Map<String, String>> )((Map<String, Object>)v).get("bids");
                    // price : 가격1, quantity : 수량1
                    // price : 가격2, quantity : 수량2 이런식으로..
                    wannaBuy.sort(Comparator
                            .comparingDouble(k1 -> Double.parseDouble(((Map<String, String>)k1).get("price"))).reversed()); // 내림차순 (비싸게 매도해야 이득)

                    for(int i=0; i<wannaBuy.size(); i++) {
                        Double price = Double.parseDouble(wannaBuy.get(i).get("price"));
                        Double quantity = Double.parseDouble(wannaBuy.get(i).get("quantity"));
                        double eachTotalPrice = price * quantity; // 특정 가격에 * 수량 매도한 금액

                        // 만약 코인 양 더 많으면 끝내기 (코인을 산 개수(availableCoin)보다 크거나 같을 경우(quantity))
                        if(quantity >= availableCoin) { // 못넘어갈경우
                            sellCurrency += price * availableCoin; // 가격 * 산 개수 = 현재 매도한 금액이 된다
                            eachOrderBook.put(price, availableCoin); // 매도 추천 창에 넣어준다
                            availableCoin = 0D; // 코인을 다 팔았으니 0D
                            break;
                        } else { // 다음 스텝 넘어갈경우
                            sellCurrency += eachTotalPrice; // 매도한 금액을 더해주고
                            eachOrderBook.put(price, quantity); // 매도 추천 창에 넣어준다.
                            availableCoin -= quantity; // 코인 개수를 감소시켜준다
                        }
                    }
                    // 모두 팔지 못했을때 조건 추가 > 넣지 말기
                    if(availableCoin == 0) { // 모두 팔았을 경우에만! 추가
                        amounts.put(coin, sellCurrency);
                        orderBooks.put(coin, eachOrderBook);
                    }
                }
            }
        });
        return new CoinSellDTO(amounts, orderBooks);
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
