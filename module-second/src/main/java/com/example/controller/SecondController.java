package com.example.controller;

import com.example.service.SecondService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/second")
@RequiredArgsConstructor
@Slf4j
public class SecondController {

    private final SecondService secondService;

    @GetMapping("/ping")
    public String ping() {
        log.info(">>> second-point .... ");
        String result = secondService.ping();
        log.info(">>> second-point .... {} ", result);
        return result;
    }

}
