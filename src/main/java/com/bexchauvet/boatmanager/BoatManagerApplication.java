package com.bexchauvet.boatmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackages = {"com.bexchauvet.boatmanager.domain"})
public class BoatManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoatManagerApplication.class, args);
    }

}
