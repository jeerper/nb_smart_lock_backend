package com.summit.service.impl;

import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.service.FaceInfoManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 根据id批量删除人脸信息
     * @param faceInfoIds
     * @return 返回-1则为不成功
     */
    @Override
    public int delFaceInfoByIds(List<String> faceInfoIds) {
        if (faceInfoIds==null || faceInfoIds.isEmpty()){
            log.error("人脸信息id列表为空");
            return CommonConstants.UPDATE_ERROR;
        }
        return faceInfoManagerDao.deleteBatchIds(faceInfoIds);
    }
}
