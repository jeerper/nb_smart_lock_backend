package com.summit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.City;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Province;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.CityDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.dao.repository.ProvinceDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.PageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */
@Slf4j
@Service
public class FaceInfoManagerServiceImpl implements FaceInfoManagerService {
    @Autowired
    private FaceInfoManagerDao faceInfoManagerDao;
    @Autowired
    private ProvinceDao provinceDao;
    @Autowired
    private CityDao cityDao;

    /**
     * 插入人脸信息
     * @param faceInfo 人脸信息
     * @return 返回-1则为不成功
     */
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
        for(String faceId:faceInfoIds){
            FaceInfo faceInfo= faceInfoManagerDao.selectFaceInfoByID(faceId);
            if(faceInfo==null|| StrUtil.isBlank(faceInfo.getFaceImage())){
                continue;
            }
            String faceImageFilePath=StrUtil.replace(faceInfo.getFaceImage(), "/", File.separator);
            try{
                FileUtil.del(SystemUtil.getUserInfo().getCurrentDir()+faceImageFilePath);
            }catch(Exception ioException){
                log.error("路径文件:"+faceImageFilePath+"删除失败!",ioException);
            }
        }
        return faceInfoManagerDao.deleteBatchIds(faceInfoIds);
    }
    /**
     * 分页查询全部人脸信息
     * @param faceInfoManagerEntity 人脸对象
     * @param page 分页对象
     * @return 人脸信息列表
     */
    @Override
    public Page<FaceInfo> selectFaceInfoByPage(FaceInfoManagerEntity faceInfoManagerEntity, SimplePage page) {
        if(faceInfoManagerEntity==null){
            log.error("人脸信息对象为空");
            return null;
        }
        List<FaceInfo> faceInfoList=faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity,null);
       // System.out.println(faceInfoList+"qqq");
        //faceInfoList=null等于0,不等于null的时候为他本身的大小
        int rowsCount = faceInfoList == null ? 0 : faceInfoList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);
        Page<FaceInfo> backpage=new Page<>();
        List<FaceInfo> faceInfoList1=faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity,page);
        //System.out.println(faceInfoList1+"wwww");
        backpage.setContent(faceInfoList1);
        backpage.setPageable(pageable);
        return backpage;
    }

    /**
     * 更新人脸信息
     * @param faceInfo 需要修改的人脸信息对象
     * @return 不为-1则为修改成功
     */
    @Override
    public int updateFaceInfo(FaceInfo faceInfo) {
        if(faceInfo == null){
            log.error("人脸信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<FaceInfo> updateWrapper=new UpdateWrapper<>();
        return  faceInfoManagerDao.update(faceInfo,updateWrapper.eq("face_id",faceInfo.getFaceid()));
    }

    /**
     * 根据人脸id查询唯一的人脸信息记录
     * @param faceid
     * @return 确定唯一的人脸信息记录
     */
    @Override
    public FaceInfo selectFaceInfoByID(String faceid) {
        if (faceid==null){
            log.error("人脸信息的id为空");
            return null;
        }
        return faceInfoManagerDao.selectFaceInfoByID(faceid);
    }

    /**
     * 查询所有的人脸信息
     * @param page
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfo> selectAllFaceInfo(SimplePage page) {
        List<FaceInfo> faceInfos= faceInfoManagerDao.selectAllFaceInfo();
        return faceInfos;
    }

    /**
     * 查询所有的省份
     * @param page
     * @return 省份信息列表
     */
    @Override
    public List<Province> selectProvince(SimplePage page) {
        QueryWrapper<Province> wrapper=new QueryWrapper<>();
        return provinceDao.selectList(wrapper);
    }

    @Override
    public List<City> selectCityByProvinceId(String provinceId) {
        if(provinceId==null){
            log.error("省份编号为空");
            return null;
        }
        QueryWrapper<City> wrapper=new QueryWrapper<>();
        return cityDao.selectList(wrapper.eq("provinceid",provinceId));
    }

    @Override
    public FaceInfo selectFaceInfoByUserNameAndCardId(String userName,String cardId) {
        if(userName==null){
            log.error("用户名为空");
            return null;
        }
        QueryWrapper<FaceInfo> wrapper=new QueryWrapper<>();
        FaceInfo faceInfo = faceInfoManagerDao.selectOne(wrapper.eq("user_name", userName).eq("card_id",cardId));
        return faceInfo;
    }


}
