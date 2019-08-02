package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FileInfo;
import com.summit.dao.entity.Page;
import com.summit.dao.repository.FaceInfoDao;
import com.summit.dao.repository.FileInfoDao;
import com.summit.service.FaceInfoService;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FaceInfoServiceImpl implements FaceInfoService {
    @Autowired
    private FaceInfoDao faceInfoDao;
    @Autowired
    private FileInfoDao fileInfoDao;

    @Override
    public int insertFaceInfo(FaceInfo faceInfo) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return -1;
        }
        int result = faceInfoDao.insertFace(faceInfo);
        if(result != -1){
            fileInfoDao.insert(faceInfo.getFacePanorama());
            fileInfoDao.insert(faceInfo.getFacePic());
            fileInfoDao.insert(faceInfo.getFaceMatch());
        }
        return result;
    }

    @Override
    public int updateFaceInfo(FaceInfo faceInfo) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return -1;
        }
        int result = faceInfoDao.updateFace(faceInfo);
        if(result != -1){
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            fileInfoDao.update(faceInfo.getFacePanorama(),updateWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.update(faceInfo.getFacePic(),updateWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.update(faceInfo.getFaceMatch(),updateWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }
    @Override
    public int delFaceInfoById(String faceId) {
        if(faceId == null){
            log.error("人脸信息id为空");
            return -1;
        }
        FaceInfo faceInfo = faceInfoDao.selectFaceById(faceId);
        UpdateWrapper<FaceInfo> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = faceInfoDao.delete(wrapper.eq("face_d", faceId));

        if(result != -1){
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }

    @Override
    public int delFaceInfoByUserName(String userName) {
        if(userName == null){
            log.error("操作人名称为空");
            return -1;
        }
        FaceInfo faceInfo = faceInfoDao.selectByName(userName);
        UpdateWrapper<FaceInfo> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = faceInfoDao.delete(wrapper.eq("user_name", userName));

        if(result != -1){
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }

    @Override
    public FaceInfo selectFaceInfoById(String faceId) {
        if(faceId == null){
            log.error("人脸信息id为空");
            return null;
        }
        return faceInfoDao.selectFaceById(faceId);
    }

    @Override
    public FaceInfo selectByUserName(String userName) {
        if(userName == null){
            log.error("操作人名称为空");
            return null;
        }
        return faceInfoDao.selectByName(userName);
    }

    @Override
    public FaceInfo selectByUserId(String userId) {
        if(userId == null){
            log.error("操作人id为空");
            return null;
        }
        return faceInfoDao.selectByUserId(userId);
    }

    @Override
    public List<FaceInfo> selectAll(Page page) {
        PageConverter.convertPage(page);
        return faceInfoDao.selectCondition(new FaceInfo(),null,null,page);
    }

    @Override
    public List<FaceInfo> selectCondition(FaceInfo faceInfo, Page page) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return null;
        }
        return selectCondition(faceInfo,null,null,page);
    }

    @Override
    public List<FaceInfo> selectCondition(FaceInfo faceInfo, Date start, Date end, Page page) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return faceInfoDao.selectCondition(faceInfo,start,end,page);
    }
}
