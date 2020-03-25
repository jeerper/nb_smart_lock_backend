package com.summit.service;

import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlService {

    /**
     * 根据门禁id查询所有的人脸信息列表
     * @param accCtrlIds
     * @return 人脸信息列表
     */
    List<FaceInfo> selectFaceInfoAccCtrlByActrlIds(List<String> accCtrlIds);

    /**
     * 根据人脸名称和身份证号查询门禁的id
     * @param userName 人脸名称
     * @param accessControlId 门禁ID
     * @return  返回门禁的id列表
     */
    int selectAccCtrlIdByUserNameAndAccessControlId(String userName,String accessControlId);




    /**
     * 根据人脸id 删除门禁授权记录
     * @param faceid
     * @return -1删除不成功
     */
    int deleteFaceAccCtrlByFaceId(String faceid);

    /**
     * 根据部门id查询当前以及子部门下的人脸信息
     * @param deptIds
     * @return
     */
    List<FaceInfo> selectAllFaceByDeptId(List<String> deptIds);

    /**
     * 根据所传部门Id查询当前以及子部门下的门禁信息
     * @param deptIds
     * @return
     */
    List<AccessControlInfo> selectAllAccCtrlByDeptId(List<String> deptIds);

    int refreshAccCtrlFaceBatch(List<String> accessControlIds, List<String> faceids) throws Exception;
}
