package com.andinaseguros.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.andinaseguros")
public class AndinaSegurosApplication {
    public static void main(String[] args) {
        SpringApplication.run(AndinaSegurosApplication.class, args);
    }
}
