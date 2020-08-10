package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class SecondService {

    public String ping(){
        log.info(">>> second service ... end");
        return "request ping success!!";
    }

}
