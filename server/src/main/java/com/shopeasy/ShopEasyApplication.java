package com.shopeasy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shopeasy.module")
public class ShopEasyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopEasyApplication.class, args);
    }
}
