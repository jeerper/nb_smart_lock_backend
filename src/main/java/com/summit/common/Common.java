package com.summit.common;

import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;

/**
 * 常量类
 * 
 * @author ChengHu
 * CreateTime:2014-4-1上午11:30:56
 */
public class Common {

	public static UserInfo getLogUser(){
		UserInfo userInfo = UserContextHolder.getUserInfo();
//		UserInfo se = new UserInfo();
//		se.setName("admin");
//		se.setUserName("admin");
//		se.setPhoneNumber("18000000000");
//		se.setEmail("123@163.com");
		return userInfo;
	}

}












