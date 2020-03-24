package com.summit.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.summit.dao.entity.FaceDept;
import com.summit.dao.repository.DeptFaceDao;
import com.summit.service.DeptFaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeptFaceServiceImpl implements DeptFaceService {
    @Autowired
    private DeptFaceDao deptFaceDao;

    /**
     * 返回-1则为不成功
     * @param deptId
     * @param face_id
     * @return
     */
    @Override
    public int insert(String deptId, String face_id) {
        if (StrUtil.isNotBlank(deptId)){
            FaceDept faceDept=new FaceDept();
            faceDept.setDeptId(deptId);
            faceDept.setFaceId(face_id);
            int insert = deptFaceDao.insert(faceDept);
        }
        return 0;
    }

    @Override
    public int delDeptFaceByFaceIdBatch(String faceId) {
        int i = deptFaceDao.delete( Wrappers.<FaceDept>lambdaQuery().eq(FaceDept::getFaceId,faceId));
        return i;
    }
}
