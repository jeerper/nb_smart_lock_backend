package com.summit.hystric;

import com.summit.service.DemoService;
import org.springframework.stereotype.Component;

@Component
public class DemoServiceHystric implements DemoService {

	@Override
	public String demo(String name) {
		// TODO Auto-generated method stub
		return "sorry" + name +" the request got error";
	}

}
