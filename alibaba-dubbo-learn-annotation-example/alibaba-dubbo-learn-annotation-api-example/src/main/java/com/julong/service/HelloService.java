package com.julong.service;

import com.julong.entity.UserInfo;

/**
 * 测试接口
 * @author julong
 * @date 2020年2月24日 下午7:10:50
 * @desc 
 */
public interface HelloService {

	/**
	 * 测试方法
	 * @param userInfo
	 * @return
	 * @throws Exception
	 * @author julong
	 * @date 2020年2月24日 下午7:10:56
	 * @desc
	 */
	public abstract String sayHello(UserInfo userInfo) throws Exception;
}
