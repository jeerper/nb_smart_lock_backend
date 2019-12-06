package com.summit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.summit.MainAction;
import com.summit.cbb.utils.page.Page;
import com.summit.cbb.utils.page.Pageable;
import com.summit.common.util.ApplicationContextUtil;
import com.summit.constants.CommonConstants;
import com.summit.dao.entity.City;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Province;
import com.summit.dao.entity.SimplePage;
import com.summit.dao.repository.CityDao;
import com.summit.dao.repository.FaceInfoManagerDao;
import com.summit.dao.repository.ProvinceDao;
import com.summit.entity.FaceInfoManagerEntity;
import com.summit.service.FaceInfoAccCtrlService;
import com.summit.service.FaceInfoManagerService;
import com.summit.util.CommonUtil;
import com.summit.util.PageConverter;
import com.summit.utils.BaiduSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @Autowired
    private FaceInfoAccCtrlService faceInfoAccCtrlService;
    @Autowired
    private BaiduSdkClient baiduSdkClient;

    /**
     * 插入人脸信息
     *
     * @param faceInfoManagerEntity 人脸信息
     * @return 返回-1则为不成功
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void insertFaceInfo(FaceInfoManagerEntity faceInfoManagerEntity) throws Exception {
        String base64Str = faceInfoManagerEntity.getFaceImage();
        if (StrUtil.isBlank(base64Str)) {
            log.error("人脸图片为空");
            throw new Exception("人脸图片不能为空");
        }
        int i = base64Str.indexOf(",");
        String subNewImageBase64 = base64Str.substring(i + 1);

        if (!baiduSdkClient.detectFace(subNewImageBase64)) {
            throw new Exception("没有检测到人脸");
        }

        byte[] subNewImageBase64Byte = Base64.getDecoder().decode(subNewImageBase64);

        //判断人脸图片是否在人脸库中存在
        List<FaceInfo> faceInfoLibrary = this.selectAllFaceInfo(null);
        for (FaceInfo faceInfo : faceInfoLibrary) {
            if (StrUtil.isBlank(faceInfo.getFaceImage())) {
                continue;
            }
            String faceImagesAbsolutePath = SystemUtil.getUserInfo().getCurrentDir() + faceInfo.getFaceImage();
            try {
                byte[] faceImagesBase64 = FileUtil.readBytes(faceImagesAbsolutePath);
                if (Arrays.equals(subNewImageBase64Byte, faceImagesBase64)) {
                    throw new Exception("人脸添加失败,头像重复");
                }
            } catch (IORuntimeException e) {
                log.error("本地人脸库图片丢失,图片路径：" + faceImagesAbsolutePath);
            }
        }

        StringBuffer fileName = new StringBuffer();
        fileName.append(UUID.randomUUID().toString().replaceAll("-", ""));
        if (base64Str.indexOf("data:image/png;") != -1) {
            fileName.append(".png");
        } else if (base64Str.indexOf("data:image/jpeg;") != -1) {
            fileName.append(".jpeg");
        }
        String picId = IdWorker.getIdStr();
        String facePicPath = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .append(picId)
                .append("_Face.jpg")
                .toString();
        String faceUrl = new StringBuilder()
                .append("/")
                .append(MainAction.SnapshotFileName)
                .append("/")
                .append(picId)
                .append("_Face.jpg")
                .toString();

        FaceInfo faceInfo = new FaceInfo();

        faceInfo.setFaceImage(faceUrl);

        faceInfo.setUserName(faceInfoManagerEntity.getUserName());
        faceInfo.setGender(faceInfoManagerEntity.getGender());
        faceInfo.setProvince(faceInfoManagerEntity.getProvince());
        faceInfo.setCity(faceInfoManagerEntity.getCity());
        faceInfo.setBirthday(faceInfoManagerEntity.getBirthday());
        faceInfo.setCardType(faceInfoManagerEntity.getCardType());
        faceInfo.setCardId(faceInfoManagerEntity.getCardId());
        faceInfo.setFaceType(faceInfoManagerEntity.getFaceType());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        Date date = new Date();
        String startTime = sdf.format(date);
        Date faceStartTime = sdf.parse(startTime);
        faceInfo.setFaceStartTime(faceStartTime);
        Date faceEndTime = CommonUtil.dateFormat.get().parse(faceInfoManagerEntity.getFaceEndTime());
        faceInfo.setFaceEndTime(faceEndTime);
        faceInfo.setIsValidTime(0);//人脸录入时候这时候人脸肯定未过期
        try {
            faceInfoManagerDao.insertFaceInfo(faceInfo);
            FileUtil.writeBytes(subNewImageBase64Byte, facePicPath);

            String faceId = baiduSdkClient.searchFace(subNewImageBase64);
            if (StrUtil.isNotBlank(faceId)) {
                FaceInfo similarFaceInfo = faceInfoManagerDao.selectById(faceId);
                if (similarFaceInfo != null) {
                    throw new Exception("发现人脸库中有相似的人脸，名字为：" + similarFaceInfo.getUserName() + "，不能重复录入相同的人脸");
                } else {
                    throw new Exception("发现人脸库中有相似的人脸，名字为：null，不能重复录入相同的人脸");
                }

            }

            if (!baiduSdkClient.addFace(subNewImageBase64, faceInfo.getFaceid())) {
                throw new Exception("人脸录入失败");
            }
        } catch (Exception e) {
            FileUtil.del(facePicPath);
            log.error(e.getMessage());
            if (e instanceof DuplicateKeyException) {
                throw new Exception("人脸信息录入名称已存在");
            }
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 根据id批量删除人脸信息
     *
     * @param faceInfoIds
     * @return 返回-1则为不成功
     */
    @Override
    public void delFaceInfoByIds(List<String> faceInfoIds) throws Exception {
        if (faceInfoIds==null||faceInfoIds.size()==0) {
            throw new Exception("人脸信息id为空");
        }
        try {
            for (String faceId : faceInfoIds) {
                ApplicationContextUtil.getBean(FaceInfoManagerServiceImpl.class).delFaceInfoById(faceId);
            }
        } catch (Exception e) {
            throw new Exception("人脸删除异常");
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void delFaceInfoById(String faceId) throws Exception {
        if (!baiduSdkClient.deleteFace(faceId)) {
            throw new Exception("图片删除失败");
        }
        FaceInfo faceInfo = faceInfoManagerDao.selectById(faceId);
        if (StrUtil.isNotBlank(faceInfo.getFaceImage())) {
            FileUtil.del(SystemUtil.getUserInfo().getCurrentDir() + faceInfo.getFaceImage());
        }
        faceInfoAccCtrlService.deleteFaceAccCtrlByFaceId(faceId);
        faceInfoManagerDao.deleteById(faceId);
    }

    /**
     * 分页查询全部人脸信息
     *
     * @param faceInfoManagerEntity 人脸对象
     * @param page                  分页对象
     * @return 人脸信息列表
     */
    @Override
    public Page<FaceInfo> selectFaceInfoByPage(FaceInfoManagerEntity faceInfoManagerEntity, SimplePage page) {
        if (faceInfoManagerEntity == null) {
            log.error("人脸信息对象为空");
            return null;
        }
        List<FaceInfo> faceInfoList = faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity, null);
        // System.out.println(faceInfoList+"qqq");
        //faceInfoList=null等于0,不等于null的时候为他本身的大小
        int rowsCount = faceInfoList == null ? 0 : faceInfoList.size();
        Pageable pageable = PageConverter.getPageable(page, rowsCount);
        PageConverter.convertPage(page);

        List<FaceInfo> faceInfoList1 = faceInfoManagerDao.selectFaceInfoByPage(faceInfoManagerEntity, page);
        //System.out.println(faceInfoList1+"wwww");
        Page<FaceInfo> backpage = new Page<>(faceInfoList1, pageable);
        return backpage;
    }

    /**
     * 更新人脸信息
     *
     * @param faceInfo 需要修改的人脸信息对象
     * @return 不为-1则为修改成功
     */
    @Override
    public int updateFaceInfo(FaceInfo faceInfo) {
        if (faceInfo == null) {
            log.error("人脸信息为空");
            return CommonConstants.UPDATE_ERROR;
        }
        UpdateWrapper<FaceInfo> updateWrapper = new UpdateWrapper<>();
        return faceInfoManagerDao.update(faceInfo, updateWrapper.eq("face_id", faceInfo.getFaceid()));
    }

    /**
     * 根据人脸id查询唯一的人脸信息记录
     *
     * @param faceid
     * @return 确定唯一的人脸信息记录
     */
    @Override
    public FaceInfo selectFaceInfoByID(String faceid) {
        if (faceid == null) {
            log.error("人脸信息的id为空");
            return null;
        }
        return faceInfoManagerDao.selectFaceInfoByID(faceid);
    }

    /**
     * 查询所有的人脸信息
     *
     * @param page
     * @return 人脸信息列表
     */
    @Override
    public List<FaceInfo> selectAllFaceInfo(SimplePage page) {
        List<FaceInfo> faceInfos = faceInfoManagerDao.selectAllFaceInfo();
        return faceInfos;
    }

    /**
     * 查询所有的省份
     *
     * @param page
     * @return 省份信息列表
     */
    @Override
    public List<Province> selectProvince(SimplePage page) {
        QueryWrapper<Province> wrapper = new QueryWrapper<>();
        return provinceDao.selectList(wrapper);
    }

    @Override
    public List<City> selectCityByProvinceId(String provinceId) {
        if (provinceId == null) {
            log.error("省份编号为空");
            return null;
        }
        QueryWrapper<City> wrapper = new QueryWrapper<>();
        return cityDao.selectList(wrapper.eq("provinceid", provinceId));
    }

    @Override
    public FaceInfo selectFaceInfoByUserNameAndCardId(String userName, String cardId) {
        if (userName == null) {
            log.error("用户名为空");
            return null;
        }
        QueryWrapper<FaceInfo> wrapper = new QueryWrapper<>();
        FaceInfo faceInfo = faceInfoManagerDao.selectOne(wrapper.eq("user_name", userName).eq("card_id", cardId));
        return faceInfo;
    }


}
