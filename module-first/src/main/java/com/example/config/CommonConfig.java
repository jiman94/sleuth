package com.example.config;

import brave.http.HttpTracing;
import brave.sampler.Sampler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.sleuth.annotation.TagValueResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Configuration
@Slf4j
public class CommonConfig {



    @Bean
    public Sampler defaultSampler() {

        return Sampler.ALWAYS_SAMPLE;

        /*return new Sampler() {
            @Override
            public boolean isSampled(long traceId) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                log.info(">>> set sampler");

                if (! Optional.ofNullable(attributes).isPresent()) {
                    log.info(">>> servletRequest is null");
                    return false;
                }

                return true;
                //HttpServletRequest request = attributes.getRequest();
                //return !request.getHeader("sampler").isEmpty() && request.getHeader("sampler").equals("true");

            }
        };*/
    }

    @Bean
    public TagValueResolver tagValueResolver() {
        return parameter -> "custom tag value resolver"; //  resolver = TagValueResolver.class 무조건 이 값이 태그에 들어간다.
    }

}
