package com.example.service;

import brave.*;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.internal.baggage.ExtraBaggageContext;
import brave.propagation.B3Propagation;
import brave.sampler.SamplerFunction;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.cloud.sleuth.annotation.SpanTag;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class FirstService {

    private static final String secondUri = "http://localhost:8081/second";
    private static final String thirdUri = "http://localhost:8082/third";
    private final RestTemplate restTemplate;
    private final Tracer tracer;
    private final Tracing autoTracing;
    private final CurrentSpanCustomizer currentSpanCustomizer;

    public String sendSecond() {
        log.info(">>> first service");
        tracer.nextSpan().name("newSpan-first").start();
        log.info(">>> new span test");
        ScopedSpan newSpan = tracer.startScopedSpan("newSpan-first");
        try {
            log.info(">>> new span start ... ");
            String response = restTemplate.getForObject(secondUri + "/ping", String.class);
            log.info(">>> from second-point .... response : {}", response);
            return "finish";
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            newSpan.finish();
        }
        return "error!!";
    }

    public String createNewTracer(){
        log.info(">>> first service");
        Span newSpan = tracer.newTrace().name("newSpan").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            log.info(">>> new span start with new tracer ");
            String response = restTemplate.getForObject(secondUri + "/ping", String.class);
            log.info(">>> from second-point .... response : {}", response);
            return "finish";
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            newSpan.finish();
        }
        return "error!!";
    }

    public void sendSecondForErrorTest() {
        log.info(">>> first service");
        Span nextSpan = tracer.nextSpan();
        log.info(">>> create new span");
        tracer.withSpanInScope(nextSpan);
        log.info(">>> create new span in scope");
        restTemplate.getForObject(secondUri + "/error", String.class);
    }

    public void addSpan() {
        log.info(">>> first service ... ");

        Span newSpan = tracer.newTrace().name("newSpan").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            log.info(">>> new span start ... ");
        } finally {
            newSpan.finish();
        }
    }

    public void nextSpan() {
        log.info(">>> first service ... ");
        SpanCustomizer spanCustomizer = tracer.nextSpan();
        spanCustomizer.name("my new span");
        Span nextSpan = (Span) spanCustomizer;
        //currentSpanCustomizer.tag("customizer", "true");
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(nextSpan.start())) {
            currentSpanCustomizer.tag("customizer", "true");
            log.info(">>> next span start ... ");
        } finally {
            nextSpan.finish();
        }
    }

    @NewSpan("addTagSpan")
    public void addTag(@SpanTag(key = "zeroTag", expression = "'hello characters'") String tag) {
        log.info(">>> first service ... ");
        Span span = tracer.currentSpan();
        span.tag("firstTag", "hello world");
        span.tag("secondTag", "sleuth example");
        restTemplate.getForObject(secondUri + "/ping", String.class);
    }

    public void addBaggage(String value) {
        log.info(">>> first service ... ");

        for (BaggageField baggageField : ExtraBaggageContext.getAllFields(tracer.currentSpan().context())) {
            log.info(">>> baggage : {} - {}",baggageField.name(), baggageField.getValue());
        }

        Tracing tracing = Tracing.newBuilder().currentTraceContext(Tracing.current().currentTraceContext()).propagationFactory(
                BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                        .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("first-bag")))
                        .add(BaggagePropagationConfig.SingleBaggageField.newBuilder(BaggageField.create("second-bag")).build())
                        .add(BaggagePropagationConfig.SingleBaggageField.local(BaggageField.create("user-local")))
                        .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("x-vcap-request-id")))
                        .build()
        ).build();


        Span updatedSpan = tracing.tracer().currentSpan();
        List<BaggageField> baggageFields = ExtraBaggageContext.getAllFields(updatedSpan.context());
        for (BaggageField baggageField : baggageFields) {
            log.info(">>> baggage : {} - {}",baggageField.name(), baggageField.getValue());
        }

        restTemplate.getForObject(secondUri+"/baggage", Object.class);

        //Object userName = baggageField.getValue(Tracing.current().currentTraceContext().get());
    }

    public void sampler() {
        log.info(">>> add sampler header!");
        Span span = tracer.currentSpan();

        SamplerFunction<Boolean> samplerFunction = new SamplerFunction() {
            @Override
            public Boolean trySample(Object arg) {
                return (boolean) arg;
            }
        };


        // Span nextSpan = tracer.nextSpanWithParent(samplerFunction, false, span.context());
        ScopedSpan nextSpan = tracer.startScopedSpan("my new span", new SamplerFunction() {
            @Override
            public Boolean trySample(Object arg) {
                return (boolean) arg;
            }
        }, false);

        log.info(">>> next span {} ", nextSpan.context().spanIdString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("X-B3-Sampled", "0");
        httpHeaders.set("test", "hello world!");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> result = restTemplate.postForEntity(secondUri+"/ping", request, String.class);
        log.info(">>> first service ... ping result : {} ", result.getBody());
    }


    public void sendMessage(String msg) {
        Map<String, String> params = new HashMap<>();
        params.put("msg", msg);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, httpHeaders);
        restTemplate.postForObject(thirdUri+"/publish", request, List.class);
    }

    public void spanLifeCycle() throws InterruptedException {
        // 1. normal.. start and close
        Span normalSpan = this.tracer.nextSpan().name("normal-span");
        try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(normalSpan.start())) {
            normalSpan.tag("life-cycle", "start");
            normalSpan.annotate("life-cycle-start");
            log.info(">>> newSpan start");
            Thread.sleep(10);
        } finally {
            normalSpan.finish();
        }

        Thread.sleep(5);

        // 2. continuing spans
        Span continuedSpan = this.tracer.toSpan(normalSpan.context());
        try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(continuedSpan.start())){
            continuedSpan.tag("life-cycle", "continue");
            continuedSpan.annotate("life-cycle-continue");
            log.info(">>> continuedSpan start");
            Thread.sleep(10);
        } finally {
            continuedSpan.finish();
        }

        // 2-*. 현재 scope로 span 바로 시작
        ScopedSpan scopedSpan = this.tracer.startScopedSpan("scopedSpan");
        try {
            log.info(">>> currentSpan = scopedSpan ");
            // scopedSpan.finish();

            // 3. explicit parent
            // parent span 지정 안 한 경우
            CustomThread normalThread = new CustomThread();
            normalThread.tracer = this.tracer;
            Thread t1 = new Thread(normalThread);

            // parent span 지정한 경우
            CustomThread explicitParentThread = new CustomThread();
            explicitParentThread.tracer = this.tracer;
            explicitParentThread.parentSpan = this.tracer.currentSpan();
            Thread t2 = new Thread(explicitParentThread);

            t1.start();
            t2.start();
        } finally {
            scopedSpan.finish();
        }

    }

    static class CustomThread implements Runnable {

        public Tracer tracer = null;
        public Span parentSpan = null;

        @SneakyThrows
        @Override
        public void run() {
            log.info(">>> newThread");
            if (tracer == null) {
                throw new RuntimeException("tracer가 존재하지 않습니다.");
            }

            if (parentSpan == null) {
                Span threadSpan = this.tracer.nextSpan().name("thread-span");
                try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(threadSpan.start())) {
                    log.info(">>> newThread span start");
                    threadSpan.tag("life-cycle", "explicit");
                    threadSpan.annotate("life-cycle-explicit");
                    Thread.sleep(10);
                } finally {
                    threadSpan.finish();
                }
            } else {
                ScopedSpan newSpan = null;
                try (Tracer.SpanInScope ws = this.tracer.withSpanInScope(parentSpan)){
                    newSpan = this.tracer.startScopedSpan("explicit-parent-span");
                    log.info(">>> newThread explicit-parent-span start");
                    newSpan.tag("life-cycle", "explicit-parent");
                    newSpan.annotate("life-cycle-explicit-parent");
                    Thread.sleep(10);
                } finally {
                    Optional.ofNullable(parentSpan).ifPresent(span -> span.finish());
                    Optional.ofNullable(newSpan).ifPresent(span -> span.finish());
                }
            }
        }
    }
}
