package com.ahnco.coinhub.feign.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BithumbResponse<T> {
    private String status;
    private T data;
}
