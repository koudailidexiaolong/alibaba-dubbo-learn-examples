package com.julong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

/**
 * 消费者
 * @author julong
 * @date 2020年2月25日 上午8:58:36
 * @desc 
 */
@EnableDubbo //启用dubbo配置
@SpringBootApplication
public class DubboProviderApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DubboProviderApplication.class, args);
	}

}
