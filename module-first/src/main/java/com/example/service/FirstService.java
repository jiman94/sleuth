package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
@Slf4j
public class FirstService {

    private static final String secondUri = "http://localhost:8081/second";
    private final RestTemplate restTemplate;

    public String sendSecond() {
        log.info(">>> first service");

        String response = restTemplate.getForObject(secondUri+"/ping", String.class);

        log.info(">>> from second-point .... response : {}", response);
        return "finish";
    }

}
