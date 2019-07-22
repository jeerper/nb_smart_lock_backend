package com.summit.service;

import com.summit.dao.DemoDao;
import com.summit.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
	@Autowired
    private DemoDao demoDao;


    public List<User> getAllUser1(){
		System.out.println(demoDao.getAllUser1());
        return demoDao.getAllUser1();
    }

    public List<User> getAllUser2(){
		System.out.println(demoDao.getAllUser1());
        return demoDao.getAllUser1();
    }

}
