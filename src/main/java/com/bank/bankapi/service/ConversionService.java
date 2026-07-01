package com.bank.bankapi.service;

import com.bank.bankapi.dto.ConversionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class ConversionService {

    private final RestClient restClient;

    public  ConversionService(){
        this.restClient = RestClient.builder()
                .baseUrl("https://api.frankfurter.dev/v2")
                .build();
    }

    public BigDecimal convert(String from, String to, BigDecimal amount){
        ConversionResponse response = restClient.get()
                .uri("/rate/{from}/{to}", from, to)
                .retrieve().body(ConversionResponse.class);
        return amount.multiply(response.getRate());
    }
}
