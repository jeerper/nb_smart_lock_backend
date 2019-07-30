package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AwakenlockRecords;

public interface AwakenlockRecordsDao  extends BaseMapper<AwakenlockRecords> {

    AwakenlockRecords selectById(String awakId);

    int deleteById(String awakId);

    int insert(AwakenlockRecords record);

    int update(AwakenlockRecords record);
}