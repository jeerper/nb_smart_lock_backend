package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.SafeReportData;

public interface SafeReportDataDao  extends BaseMapper<SafeReportData> {

    SafeReportData selectById(String id);

}