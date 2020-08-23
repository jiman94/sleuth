package com.example.controller;

import com.example.kafka.CustomProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/third")
@RequiredArgsConstructor
@Slf4j
public class ThirdController {

    @Autowired
    private CustomProducer producer;

    @PostMapping("/publish")
    public void producer(@RequestBody Map<String, String> message){
        log.info(">>> start event publish");
        producer.send(message.get("msg"));
    }

}
