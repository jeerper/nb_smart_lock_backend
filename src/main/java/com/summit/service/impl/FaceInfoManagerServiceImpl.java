package com.summit.service.impl;

import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.FaceInfoManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2019/8/21.
 */
@Slf4j
@Service
public class FaceInfoManagerServiceImpl implements FaceInfoManagerService {
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Override
    public int insertFaceInfo(FaceInfo faceInfo) {
        int result=faceInfoManagerDao.insertFaceInfo(faceInfo);
        return result;
    }
}
