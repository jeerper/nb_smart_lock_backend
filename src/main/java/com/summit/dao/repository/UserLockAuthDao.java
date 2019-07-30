package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.UserLockAuth;

public interface UserLockAuthDao  extends BaseMapper<UserLockAuth> {

    UserLockAuth selectById(String id);

    int deleteById(String id);

    int insert(UserLockAuth record);

    int update(UserLockAuth record);
}