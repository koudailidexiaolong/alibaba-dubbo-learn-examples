package com.julong.entity;

import java.io.Serializable;

/**
 * 用户信息
 * @author julong
 * @date 2020年2月24日 下午7:09:23
 * @desc 
 */
public class UserInfo implements Serializable{

	/**
	 * @author julong
	 * @date 2020年2月24日 下午11:32:48
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户名称
	 * @author julong
	 * @date 2020年2月24日 下午7:09:30
	 */
	private String userName;

	/**
	 * @return String the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
