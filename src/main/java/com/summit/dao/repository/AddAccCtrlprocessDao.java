package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AddAccCtrlprocess;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2019/9/16.
 */

public interface AddAccCtrlprocessDao  extends BaseMapper<AddAccCtrlprocess>{

    AddAccCtrlprocess selectAccCtrlByAccCtrlName(@Param("accessControlName") String accessControlName);
}
