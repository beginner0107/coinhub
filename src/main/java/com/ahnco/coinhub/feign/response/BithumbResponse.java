package com.ahnco.coinhub.feign.response;

import lombok.Getter;

@Getter
public class BithumbResponse<T> {
    private String status;
    private T data;
}
