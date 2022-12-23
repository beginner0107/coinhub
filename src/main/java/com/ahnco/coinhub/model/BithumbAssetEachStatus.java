package com.ahnco.coinhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BithumbAssetEachStatus {
    private int withdrawal_status;
    private int deposit_status;
}
