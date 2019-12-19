package com.summit.util;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 人脸信息全局获取工具类
 *
 * @author Administrator
 */
public class FaceInfoContextHolder {
    /**
     * 人脸信息实体存储容器.
     */
    private final static ThreadLocal<String> FACE_INFO_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 获取人脸ID
     *
     * @return
     */
    public static String getFaceId() {
        return FACE_INFO_THREAD_LOCAL.get();
    }

    /**
     * 设置人脸ID
     *
     * @param faceId 人脸ID
     */
    static void setFaceId(String faceId) {
        FACE_INFO_THREAD_LOCAL.set(faceId);
    }

    /**
     * 移除人脸信息
     */
    public static void clear() {
        FACE_INFO_THREAD_LOCAL.remove();
    }


}
