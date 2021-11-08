package com.julongtech.user.util.response;

import com.julongtech.user.util.AbstractUserResponse;

/**
 * 用户返回信息
 * @author julong
 * @date 2021年10月31日 下午8:53:33
 * @desc 
 */
public class UserResponseEntity extends AbstractUserResponse {

	/**
	 * @author julong
	 * @date 2021年10月31日 下午8:53:29
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户姓名
	 * @author julong
	 * @date 2021年10月31日 下午8:53:49
	 */
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "UserResponseEntity [userName=" + userName + "]";
	}
	
	

}
