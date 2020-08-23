package com.example;

import com.example.kafka.CustomProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThirdModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThirdModuleApplication.class, args);
    }

}
