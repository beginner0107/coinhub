package com.ahnco.coinhub.feign;

import com.ahnco.coinhub.constant.CacheConstants;
import com.ahnco.coinhub.model.UpbitCoinPrice;
import com.ahnco.coinhub.model.UpbitMarketCode;
import com.ahnco.coinhub.model.UpbitWithdrawalFee;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "upbitFee", url = "https://api-manager.upbit.com/api/v1/kv")
public interface UpbitFeeFeignClient {
    @Cacheable(CacheConstants.UPBIT_WITHDRAWAL_FEE)
    @GetMapping("/UPBIT_PC_COIN_DEPOSIT_AND_WITHDRAW_GUIDE")
    UpbitWithdrawalFee getWithdrawalFee();
}
