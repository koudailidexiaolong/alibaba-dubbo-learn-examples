package com.julong.dubbo.service.impl;

import com.julong.dubbo.service.HelloService;

/**
 * 服务调用类
 * @author julong
 * @date 2021年11月27日 上午11:30:36
 * @desc 
 */
public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String message) {
		// TODO Auto-generated method stub
		return "dubbo :" +message;
	}

}
