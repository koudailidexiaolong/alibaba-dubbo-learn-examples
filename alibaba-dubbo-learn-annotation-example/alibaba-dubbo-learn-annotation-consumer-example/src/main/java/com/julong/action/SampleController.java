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

package com.julong.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.julong.entity.UserInfo;
import com.julong.service.HelloService;

/**
 * 测试action
 * @author julong
 * @date 2020年2月25日 上午8:54:52
 * @desc 
 */
@Controller
public class SampleController {

	/**
	 * (url=192.168.10.12:20880) //可以进行直连 dubbo 生产者 provider
	 * loadbalance //可以设置负载均衡
	 */
	@Reference
	private HelloService helloServiceImpl;

	/**
	 * 测试方法 
	 * @param name 姓名
	 * @return java.lang.String
	 * @author julong
	 * @date 2020/2/25 13:28
	 * @desc  如果
	 */
	@RequestMapping("/{name}")
	@ResponseBody
	public String helloWorld(@PathVariable String name) {
		try {
			UserInfo userInfo = new UserInfo();
			userInfo.setUserName(name);
			return helloServiceImpl.sayHello(userInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
