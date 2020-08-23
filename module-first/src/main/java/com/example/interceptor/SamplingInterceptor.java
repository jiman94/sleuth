package com.example.interceptor;

import brave.Span;
import brave.Tracer;
import brave.http.HttpTracing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Slf4j
public class SamplingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    final private HttpTracing httpTracing;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Tracer tracer = httpTracing.tracing().tracer();
        log.info("now span id : {}", tracer.currentSpan().context().spanIdString());

        //tracer.startScopedSpanWithParent("new span", tracer.newTrace().start().context());

        //log.info(">>> second-point X-B3-Sampled header : {} ", request.getHeader("X-B3-Sampled"));

        //log.info("new span id : {}", tracer.currentSpan().context().spanIdString());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
