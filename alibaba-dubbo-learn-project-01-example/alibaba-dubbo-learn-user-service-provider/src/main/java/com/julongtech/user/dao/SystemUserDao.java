package com.julongtech.user.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.julongtech.user.dao.entity.SystemUserInfo;
import com.julongtech.user.service.dto.SystemUserDTO;

@Repository
public interface SystemUserDao {
	public abstract int deleteByPrimaryKey(String userId);

	public abstract int insert(SystemUserInfo record);

	public abstract int insertSelective(SystemUserInfo record);

	public abstract SystemUserDTO selectByPrimaryKey(String userId);

	public abstract List<SystemUserDTO> selectByParametersSelective(SystemUserDTO record);

	public abstract int selectCountBySelective(SystemUserInfo record);

	public abstract int updateByPrimaryKeySelective(SystemUserInfo record);

	public abstract int updateByPrimaryKey(SystemUserInfo record);

}