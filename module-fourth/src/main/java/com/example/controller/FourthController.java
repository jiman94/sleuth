package com.example.controller;

import brave.Request;
import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.http.HttpClientRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fourth")
@PropertySource(value = "classpath:application.yml")
@RequiredArgsConstructor
@Slf4j
public class FourthController {

    @Value("${remote-server.service-name}")
    private String serviceName;

    @Value("${remote-server.ip}")
    private String ip;

    @Value("${remote-server.port}")
    private String port;

    @Autowired
    final private Tracer tracer;

    @Autowired
    final private Tracing tracing;

    @GetMapping("/rpc")
    public void rpcTest(){
        log.info("service name : {}",serviceName);
        Span span = tracer.nextSpan().name("fourth-point/Get").kind(Span.Kind.CLIENT);
        span.tag("myrpc.version", "1.0.0");
        span.remoteServiceName(serviceName);
        span.remoteIpAndPort(ip, Integer.parseInt(port));

        tracer.withSpanInScope(span.start());



    }

}
