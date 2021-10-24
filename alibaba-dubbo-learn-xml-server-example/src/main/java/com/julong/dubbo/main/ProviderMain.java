package com.julong.dubbo.main;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务提供方
 * @author julong
 * @date 2021年10月22日 下午9:57:34
 * @desc 
 */
public class ProviderMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-server.xml"});
		context.start();
		try {
			System.out.println("服务启动");
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 按任意键退出
	}

}
