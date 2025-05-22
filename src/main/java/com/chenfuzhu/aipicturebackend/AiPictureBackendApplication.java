package com.chenfuzhu.aipicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.chenfuzhu.aipicturebackend.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AiPictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPictureBackendApplication.class, args);
    }

}
