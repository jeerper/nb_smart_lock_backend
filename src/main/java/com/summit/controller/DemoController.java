package com.summit.controller;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.util.ResultBuilder;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.service.DemoService;
import com.summit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Enumeration;

@Api(description = "测试模块")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/demo")
@RestController
public class DemoController {

	@Autowired
    DemoService demoService;

	@Autowired
	UserService userService;

	@ApiOperation(value = "liuyuanss")
	@GetMapping(value = "/liuyuanss")
	public RestfulEntityBySummit<String> liuyuan() {
		UserInfo userInfo= UserContextHolder.getUserInfo();
		return ResultBuilder.buildSuccess(userInfo.getUserName());
	}



	@ApiOperation(value = "hi")
	@GetMapping(value = "/hi")
	public RestfulEntityBySummit<String> sayHi(@RequestParam String name) {
		return ResultBuilder.buildSuccess(demoService.demo(name));
	}


	@ApiOperation(value = "testDemo1")
	@GetMapping("/testDemo1")
	public RestfulEntityBySummit<String> demo1(HttpServletRequest request) {
		System.out.println("进入此方法");
		//测试是否能取到请求头中的token
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String nextElement = headerNames.nextElement();
            System.out.println(nextElement+":"+request.getHeader(nextElement));
        }
		return ResultBuilder.buildSuccess(userService.getAllUser1().toString());
	}

	@ApiOperation(value = "testDemo2")
	@GetMapping("/testDemo2")
	public RestfulEntityBySummit<String> demo2() {
		return ResultBuilder.buildSuccess(userService.getAllUser2().toString());
	}

	public String getBodyString(BufferedReader br) {
		String inputLine;
		String str = "";
		try {
			while ((inputLine = br.readLine()) != null) {
				str += inputLine;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

}
