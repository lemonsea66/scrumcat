package com.scrumcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan("com.scrumcat.mapper")
@SpringBootApplication
public class ScrumCatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrumCatApplication.class, args);
    }
}
