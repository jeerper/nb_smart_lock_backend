package com.summit.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.summit.entity.FaceRecognitionInfo;

/**
 * 人脸信息全局获取工具类
 *
 * @author Administrator
 */
public class FaceInfoContextHolder {
    /**
     * 人脸信息实体存储容器.
     */
    private final static ThreadLocal<FaceRecognitionInfo> FACE_INFO_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 获取人脸ID
     *
     * @return
     */
    public static FaceRecognitionInfo getFaceRecognitionInfo() {
        return FACE_INFO_THREAD_LOCAL.get();
    }

    /**
     * 设置人脸ID
     *
     * @param faceRecognitionInfo 人脸识别信息
     */
    static void setFaceRecognitionInfo(FaceRecognitionInfo faceRecognitionInfo) {
        FACE_INFO_THREAD_LOCAL.set(faceRecognitionInfo);
    }

    /**
     * 移除人脸信息
     */
    public static void clear() {
        FACE_INFO_THREAD_LOCAL.remove();
    }


}
