package com.summit.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.domain.User;

import java.util.List;

public interface DemoDao extends BaseMapper<User> {
    //使用xml配置形式查询
    public List<User> getAllUser1();
}
