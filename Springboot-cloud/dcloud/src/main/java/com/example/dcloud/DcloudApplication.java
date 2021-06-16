package com.example.dcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@MapperScan("com.example.dcloud.mapper")
public class DcloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcloudApplication.class, args);
    }

}
