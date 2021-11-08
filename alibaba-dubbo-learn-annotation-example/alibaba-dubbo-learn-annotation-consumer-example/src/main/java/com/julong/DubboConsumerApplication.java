package com.julong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@EnableDubbo(scanBasePackages="com.julong")
@SpringBootApplication
public class DubboConsumerApplication {


	public static void main(String[] args) throws Exception {
		SpringApplication.run(DubboConsumerApplication.class, args);
	}


}
