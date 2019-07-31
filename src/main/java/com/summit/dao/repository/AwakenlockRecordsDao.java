package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AwakenlockRecords;

public interface AwakenlockRecordsDao  extends BaseMapper<AwakenlockRecords> {

    AwakenlockRecords selectAwakenRecordById(String awakId);

}