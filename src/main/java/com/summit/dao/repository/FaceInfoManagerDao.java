package com.summit.dao.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.summit.dao.entity.FaceInfo;
import com.summit.entity.FaceInfoManagerEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2019/8/21.
 */
public interface FaceInfoManagerDao extends BaseMapper<FaceInfo> {
    int insertFaceInfo(FaceInfo faceInfo);

    List<FaceInfo> selectFaceInfoByPage(@Param("faceInfoManagerEntity") FaceInfoManagerEntity faceInfoManagerEntity, Page page, @Param("depts")List<String> depts,@Param("userDepts") List<String> userDepts);

    FaceInfo selectFaceInfoByID(@Param("faceid") String faceid,@Param("depts") List<String> depts);

    String selectFaceIdByUserName(@Param("userName") String userName);

    List<FaceInfo> selectAllFaceInfo();

    List<FaceInfo> faceInfoExport();


    void insertFaceInfos(@Param("faceInfos") List<FaceInfo> faceInfos);

    List<FaceInfo> selectAllFaceByDeptId(@Param("depts") List<String> depts);

    FaceInfo selectFaceInfoById(@Param("faceid")String faceId);
}
