package com.example.controller;

import com.example.service.FirstService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanName;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/first")
@RequiredArgsConstructor
@Slf4j
public class FirstController {

    @Autowired
    private final FirstService firstService;

    final static private String SUCCESSMSG = "success!";

    @GetMapping("/start")
    public String start(@RequestParam String type) {
        log.info(">>> start .. first controller ... ");
        if (type.equals("newSpan")){
            log.info(">>> first controller ... {}", firstService.sendSecond());
        } else {
            log.info(">>> first controller ... {}", firstService.createNewTracer());
        }
        return SUCCESSMSG;
    }

    @GetMapping("/error")
    public String error() {
        log.info(">>> first point ");
        firstService.sendSecondForErrorTest();
        return SUCCESSMSG;
    }

    @GetMapping("/add_tag")
    public String addTag() {
        firstService.addTag("annotation tag");
        return SUCCESSMSG;
    }

    @GetMapping("/add_span")
    public String addSpan(@RequestParam String spanType) {
        log.info(">>> addSpan request start ... ");
        if (spanType.equals("newTrace")){
            firstService.addSpan();
        } else if (spanType.equals("nextSpan")){
            firstService.nextSpan();
        }
        log.info(">>> addSpan request end ... ");
        return SUCCESSMSG;
    }

    @GetMapping("/add_baggage")
    public String addBaggage(@RequestParam String baggage){
        log.info(">>> add baggage request start ... ");
        firstService.addBaggage(baggage);
        log.info(">>> add baggage request end ... ");
        return SUCCESSMSG;
    }

    @PostMapping("/kafka_event")
    public String kafkaEvent(@RequestBody Map<String, String> message){
        log.info(">>> root span");
        firstService.sendMessage(message.get("msg"));
        return SUCCESSMSG;
    }

    @GetMapping("/sampling")
    public String sampling() {
        log.info(">>> root span");
        firstService.sampler();
        return SUCCESSMSG;
    }

    @GetMapping("/life_cycle")
    public String spanLifeCycle() throws InterruptedException {
        firstService.spanLifeCycle();
        return SUCCESSMSG;
    }

}
