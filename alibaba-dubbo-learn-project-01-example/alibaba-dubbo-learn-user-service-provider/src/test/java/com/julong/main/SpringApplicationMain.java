package com.julong.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * spring 启动类
 * @author julong
 * @date 2021年11月8日 上午9:47:52
 * @desc 
 */
public class SpringApplicationMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring/applicationContext.xml"});
		context.start();
		System.out.println("启动成功");
		
//		UserService userServiceImpl = (UserService) context.getBean(UserService.class);
//		System.out.println(userServiceImpl);
//		SystemUserDTO systemUserDTO = new SystemUserDTO();
//		systemUserDTO.setUserId("admin");
		
		try {
			System.in.read();
//			systemUserDTO = userServiceImpl.login(systemUserDTO);
//			System.out.println(systemUserDTO.toString());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
