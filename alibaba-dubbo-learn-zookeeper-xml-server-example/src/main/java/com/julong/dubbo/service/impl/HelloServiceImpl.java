package com.julong.dubbo.service.impl;

import com.julong.dubbo.service.HelloService;

public class HelloServiceImpl implements HelloService {

	@Override
	public String sayHello(String message) {
		// TODO Auto-generated method stub
		return "dubbo :" +message;
	}

}
