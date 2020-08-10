package com.example.controller;

import com.example.service.FirstService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/first")
@RequiredArgsConstructor
@Slf4j
public class FirstController {

    @Autowired
    private final FirstService firstService;

    @GetMapping("/start")
    public String start() {
        log.info(">>> start .. first controller ... ");
        String result = firstService.sendSecond();
        log.info(">>> first controller ... {}",result);
        return result;
    }

}
