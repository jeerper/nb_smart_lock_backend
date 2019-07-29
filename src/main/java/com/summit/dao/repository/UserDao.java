package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.User;

public interface UserDao extends BaseMapper<User> {

    /**
     * 使用xml配置形式查询
     * @return
     */
    public User getUserInfoByUserName();



}
