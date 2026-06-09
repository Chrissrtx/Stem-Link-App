package com.example.stemlinkapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StemLinkAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StemLinkAppApplication.class, args);
    }
}