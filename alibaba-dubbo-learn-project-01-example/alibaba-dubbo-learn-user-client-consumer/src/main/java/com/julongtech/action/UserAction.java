package com.julongtech.action;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julongtech.user.service.UserService;
import com.julongtech.user.service.dto.SystemUserDTO;

@Controller
public class UserAction {

	@Autowired
	private UserService userService;
	
	/**
	 * 登录的方法
	 * @return
	 * @author julong
	 * @date 2021年11月2日 下午9:09:49
	 * @desc
	 */
	@RequestMapping("/login")
	public String login(Model model){
		SystemUserDTO systemUserDTO = new SystemUserDTO();
		systemUserDTO.setUserId("admin");
		try {
			systemUserDTO = this.userService.login(systemUserDTO);
			model.addAttribute("user", new ObjectMapper().writeValueAsString(systemUserDTO));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "index";
	}
	
}
