package com.taireum.ccc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties({RpcConfig.class})
@SpringBootApplication
public class CccApplication {
    public static void main(String[] args) {
        SpringApplication.run(CccApplication.class, args);
    }
}
