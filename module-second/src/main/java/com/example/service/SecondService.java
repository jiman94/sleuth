package com.example.service;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.baggage.BaggageField;
import brave.internal.baggage.ExtraBaggageContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class SecondService {

    private final Tracer tracer;

    public String ping(){
        ScopedSpan span = tracer.startScopedSpan("newSpan-second");
        try {
            log.info(">>> second service ... end");
        } finally {
            span.finish();
        }
        return "request ping success!!";
    }

    public String createError() {
        log.info(">>> second service ... ");
        throw new RuntimeException("sleuth error log test");
    }

    public void findBaggage() {
        Span updatedSpan = tracer.currentSpan();
        Long parentId = updatedSpan.context().parentId();
        log.info("parents Id : {}", parentId);
        List<BaggageField> baggageFields = ExtraBaggageContext.getAllFields(updatedSpan.context());
        for (BaggageField baggageField : baggageFields) {
            log.info(">>> second span.. baggage : {} - {}",baggageField.name(), baggageField.getValue());
        }
    }

}
