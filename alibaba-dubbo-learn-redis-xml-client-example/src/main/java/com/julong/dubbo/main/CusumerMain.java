package com.julong.dubbo.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.julong.dubbo.service.HelloService;

/**
 * 服务调用方
 * @author julong
 * @date 2021年10月22日 下午9:59:52
 * @desc 
 */
public class CusumerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"dubbo-client.xml"});
		context.start();
		HelloService helloService = (HelloService)context.getBean("helloService"); // 获取远程服务代理
		String hello = helloService.sayHello("hello world"); // 执行远程方法
		System.out.println( hello ); // 显示调用结果
	}

}
