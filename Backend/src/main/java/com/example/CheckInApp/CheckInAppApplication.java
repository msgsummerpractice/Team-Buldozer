package com.example.CheckInApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CheckInAppApplication {

    static void main(String[] args) {
        SpringApplication.run(CheckInAppApplication.class, args);
    }

}
