package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AddAccCtrlprocess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2019/9/16.
 */

public interface AddAccCtrlprocessDao  extends BaseMapper<AddAccCtrlprocess>{

    AddAccCtrlprocess selectAccCtrlByAccCtrlName(@Param("accessControlName") String accessControlName);

    List<AddAccCtrlprocess> selectAddAccCtrlprocessDesc();

    int updateAddAccCtrlprocess(@Param("accessControlId") String accessControlId,@Param("accessControlName") String accessControlName);
}
