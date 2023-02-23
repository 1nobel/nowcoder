package com.fct.nowcoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
public class NowcoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NowcoderApplication.class, args);
    }

}
