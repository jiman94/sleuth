package com.example.controller;

import com.example.service.SecondService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

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

    @PostMapping("/ping")
    public String pingPost() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(">>> second-point X-B3-Sampled header : {} ", request.getHeader("X-B3-Sampled"));
        log.info(">>> second-point test header : {} ", request.getHeader("test"));
        String result = secondService.ping();
        log.info(">>> second-point .... {} ", result);
        return result;
    }

    @GetMapping("/error")
    private void error() {
        log.info(">>> second point ");
        secondService.createError();
    }

    @GetMapping("/baggage")
    public void baggage() {
        log.info(">>> second-point .... ");
        secondService.findBaggage();
        log.info(">>> second-point end");
    }

}
