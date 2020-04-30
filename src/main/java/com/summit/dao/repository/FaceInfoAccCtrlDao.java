package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.summit.dao.entity.AccessControlInfo;
import com.summit.dao.entity.FaceInfo;
import com.summit.dao.entity.FaceInfoAccCtrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门禁和人脸授权信息关联表Dao
 * Created by Administrator on 2019/8/29.
 */
public interface FaceInfoAccCtrlDao extends BaseMapper<FaceInfoAccCtrl> {
    int selectCountAccCtrlIdByFaceIdAndAccessControlId(@Param("faceid") String faceid,@Param("accessControlId") String accessControlId);
    int selectCountByFaceIdAndLockCode(@Param("faceId") String faceid,@Param("lockCode") String lockCode);

    List<AccessControlInfo> selectAllAccCtrlByDeptId(@Param("depts") List<String> depts);

    List<FaceInfo> selectFaceInfoAccCtrlByActrlIds(@Param("accCtrlIds")List<String> accCtrlIds,@Param("userDepts")List<String> userDepts);

    int selectCountByUserNameAndLockCode(@Param("userName") String userName,@Param("lockCode") String lockCode);

}
