package com.julongtech.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julongtech.user.dao.SystemUserDao;
import com.julongtech.user.service.UserService;
import com.julongtech.user.service.dto.SystemUserDTO;

/**
 * 用户服务接口
 * @author julong
 * @date 2021年10月31日 下午8:44:47
 * @desc 
 */
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private SystemUserDao systemUserDaoImpl;
	
	/**
	 * 用户登录的方法
	 * @param userDTO
	 * @return
	 * @throws Exception
	 * @author julong
	 * @date 2021年10月31日 下午8:47:20
	 * @desc
	 */
	@Override
	public SystemUserDTO login(SystemUserDTO systemUserDTO) throws Exception {
		// TODO Auto-generated method stub
		
//		UserResponseEntity userResponseEntity = new UserResponseEntity();
//		userResponseEntity
		return this.systemUserDaoImpl.selectByParametersSelective(systemUserDTO).get(0);
	}

	
}
