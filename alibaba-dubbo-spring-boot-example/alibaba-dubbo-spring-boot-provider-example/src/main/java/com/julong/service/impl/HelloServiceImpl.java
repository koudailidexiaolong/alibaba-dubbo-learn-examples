/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.julong.service.impl;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.julong.entity.UserInfo;
import com.julong.service.HelloService;

/**
 * 测试接口实现类
 * @author julong
 * @date 2020年2月24日 下午8:11:13
 * @desc 
 */
@Service //dubbo的注解
@Component
public class HelloServiceImpl implements HelloService{

	@Override
	public String sayHello(UserInfo userInfo) throws Exception {
		// TODO Auto-generated method stub
		return "你好！" + userInfo.getUserName();
	}


}
