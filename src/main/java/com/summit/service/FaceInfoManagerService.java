package com.summit.service;

import com.summit.cbb.utils.page.Page;
import com.summit.dao.entity.City;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.Province;
import com.summit.dao.entity.SimplePage;
import com.summit.entity.FaceInfoManagerEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */

/**
 * 人脸信息service接口
 */
public interface FaceInfoManagerService {

    /**
     * 人脸信息插入
     * @param faceInfoManagerEntity 人脸信息
     */
    void insertFaceInfo(FaceInfoManagerEntity faceInfoManagerEntity) throws Exception;

    /**
     * 根据id批量删除人脸信息
     * @param faceInfoIds
     * @return 返回-1则为不成功
     */
    void delFaceInfoByIds(List<String> faceInfoIds)throws Exception;

    /**
     * 根据ID删除人脸信息
     * @param faceId 人脸ID
     * @throws Exception
     */
    void delFaceInfoById(String faceId) throws Exception ;

    /**
     * 分页查询人脸信息
     * @param faceInfoManagerEntity 人脸对象
     * @param  simplePage 分页对象
     * @return  人脸信息列表分页对象
     */
    Page<FaceInfo> selectFaceInfoByPage(FaceInfoManagerEntity faceInfoManagerEntity, SimplePage simplePage);

    /**
     * 更新人脸信息
     * @param faceInfo 需要修改的人脸信息对象
     * @return 返回-1则为不成功
     */
    void updateFaceInfo(FaceInfo faceInfo)throws Exception;

    /**
     * 更具faceid查询唯一一条人脸信息
     * @param faceid
     * @return 唯一确定的人脸信息记录
     */
    FaceInfo selectFaceInfoByID(String faceid);

    /**
     * 查询所有的省份
     * @param page
     * @return 省份列表
     */
    List<Province> selectProvince(SimplePage page);

    /**
     * 根据省份的编号查询省份所对应的所有的城市
     * @param provinceId
     * @return 城市列表
     */
    List<City> selectCityByProvinceId(String provinceId);

    /**
     * 根据用户名查询用户对象
     * @param userName
     * @return 城市列表
     */
    FaceInfo selectFaceInfoByUserNameAndCardId(String userName,String cardId);

    List<FaceInfo> faceInfoExport();


    boolean batchImport(MultipartFile file) throws Exception;

}
