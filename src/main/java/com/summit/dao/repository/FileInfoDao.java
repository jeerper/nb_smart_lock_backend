package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.FileInfo;

public interface FileInfoDao  extends BaseMapper<FileInfo> {

    FileInfo selectFileById(String fileId);


}