package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.UserLockAuth;

public interface UserLockAuthDao  extends BaseMapper<UserLockAuth> {

    UserLockAuth selectUserLockAuthById(String id);

}