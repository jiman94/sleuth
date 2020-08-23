package com.example.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomProducerTest {

    @Autowired
    private CustomProducer sut;

    @Test
    void test1(){
        sut.send("this message sent from spring boot application!");
    }

}
