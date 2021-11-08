package com.julongtech.user.service;

import com.julongtech.user.service.dto.SystemUserDTO;

/**
 * 用户服务接口
 * @author julong
 * @date 2021年10月31日 下午8:44:47
 * @desc 
 */
public interface UserService {

	/**
	 * 用户登录的方法
	 * @param systemUserDTO
	 * @return
	 * @throws Exception
	 * @author julong
	 * @date 2021年10月31日 下午8:47:20
	 * @desc
	 */
	public abstract SystemUserDTO login(SystemUserDTO systemUserDTO) throws Exception;
}
