package com.julongtech.user.util;

import java.io.Serializable;

/**
 * 用户服务响应抽象类
 * @author julong
 * @date 2021年10月31日 下午8:53:00
 * @desc 
 */
public abstract class AbstractUserResponse implements Serializable{

	/**
	 * @author julong
	 * @date 2021年10月31日 下午8:53:17
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 返回消息
	 * @author julong
	 * @date 2021年10月31日 下午8:51:40
	 */
	private String message;
	
	/**
	 * 返回数据
	 * @author julong
	 * @date 2021年10月31日 下午8:51:51
	 */
	private Object data;
	
	/**
	 * 响应码
	 * @author julong
	 * @date 2021年10月31日 下午8:52:10
	 */
	private int code;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "AbstractUserResponse [message=" + message + ", data=" + data + ", code=" + code + "]";
	}
	
	
}
