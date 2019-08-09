package com.summit.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.FaceInfoEntity;
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

    /**
     * 插入人脸信息
     * @param faceInfo 人脸信息对象
     * @return 不为-1则为成功
     */
    @Override
    public int insertFaceInfo(FaceInfoEntity faceInfo) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = faceInfoDao.insertFace(faceInfo);
        if(result != CommonConstants.UPDATE_ERROR){
            fileInfoDao.insert(faceInfo.getFacePanorama());
            fileInfoDao.insert(faceInfo.getFacePic());
            fileInfoDao.insert(faceInfo.getFaceMatch());
        }
        return result;
    }

    /**
     * 更新人脸信息
     * @param faceInfo 人脸信息对象
     * @return 不为-1则为成功
     */
    @Override
    public int updateFaceInfo(FaceInfoEntity faceInfo) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        int result = faceInfoDao.updateFace(faceInfo);
        if(result != CommonConstants.UPDATE_ERROR){
            UpdateWrapper<FileInfo> updateWrapper = new UpdateWrapper<>();
            fileInfoDao.update(faceInfo.getFacePanorama(),updateWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.update(faceInfo.getFacePic(),updateWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.update(faceInfo.getFaceMatch(),updateWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 根据人脸信息id删除
     * @param faceId 人脸信息id
     * @return 不为-1则为成功
     */
    @Override
    public int delFaceInfoById(String faceId) {
        if(faceId == null){
            log.error("人脸信息id为空");
            return CommonConstants.UPDATE_ERROR;
        }
        FaceInfoEntity faceInfo = faceInfoDao.selectFaceById(faceId);
        UpdateWrapper<FaceInfoEntity> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = faceInfoDao.delete(wrapper.eq("face_d", faceId));

        if(result != CommonConstants.UPDATE_ERROR){
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 根据人脸信息对应姓名删除
     * @param userName 人脸信息对应姓名
     * @return 不为-1则为成功
     */
    @Override
    public int delFaceInfoByUserName(String userName) {
        if(userName == null){
            log.error("操作人名称为空");
            return CommonConstants.UPDATE_ERROR;
        }
        FaceInfoEntity faceInfo = faceInfoDao.selectByName(userName);
        UpdateWrapper<FaceInfoEntity> wrapper = new UpdateWrapper<>();
        UpdateWrapper<FileInfo> fileWrapper = new UpdateWrapper<>();
        int result = faceInfoDao.delete(wrapper.eq("user_name", userName));

        if(result != CommonConstants.UPDATE_ERROR){
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePanorama().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFacePic().getFileId()));
            fileInfoDao.delete(fileWrapper.eq("file_id",faceInfo.getFaceMatch().getFileId()));
        }
        return result;
    }

    /**
     * 根据id查询唯一人脸信息
     * @param faceId 人脸信息id
     * @return 唯一人脸信息
     */
    @Override
    public FaceInfoEntity selectFaceInfoById(String faceId) {
        if(faceId == null){
            log.error("人脸信息id为空");
            return null;
        }
        return faceInfoDao.selectFaceById(faceId);
    }

    /**
     * 根据姓名查询唯一人脸信息
     * @param userName 姓名
     * @return 唯一人脸信息
     */
    @Override
    public FaceInfoEntity selectByUserName(String userName) {
        if(userName == null){
            log.error("操作人名称为空");
            return null;
        }
        return faceInfoDao.selectByName(userName);
    }

    /**
     * 根据userId查询唯一人脸信息
     * @param userId 人脸信息对应userId
     * @return 唯一人脸信息
     */
    @Override
    public FaceInfoEntity selectByUserId(String userId) {
        if(userId == null){
            log.error("操作人id为空");
            return null;
        }
        return faceInfoDao.selectByUserId(userId);
    }

    /**
     * 分页查询所有人脸信息
     * @param page 分页对象
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfoEntity> selectAll(Page page) {
        PageConverter.convertPage(page);
        return faceInfoDao.selectCondition(new FaceInfoEntity(),null,null,page);
    }

    /**
     * 条件查询人脸信息，不带时间重载
     * @param faceInfo 人脸信息对象
     * @param page 分页对象
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, Page page) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return null;
        }
        return selectCondition(faceInfo,null,null,page);
    }

    /**
     * 条件查询人脸信息,可指定时间段
     * @param faceInfo 人脸信息对象
     * @param start 开始时间
     * @param end 截止时间
     * @param page 分页对象
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfoEntity> selectCondition(FaceInfoEntity faceInfo, Date start, Date end, Page page) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return null;
        }
        PageConverter.convertPage(page);
        return faceInfoDao.selectCondition(faceInfo,start,end,page);
    }
}
