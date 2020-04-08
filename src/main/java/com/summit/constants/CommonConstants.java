package com.summit.constants;

public class CommonConstants {

    /**
     * 增删改操作异常
     */
    public static final Integer UPDATE_ERROR = -1;

    /**
     * 增删改操作成功
     */
    public static final Integer UPDATE_SUS = 0;

    /**
     * 开始时间标志
     */
    public static final String STARTTIMEMARK = "s";

    /**
     * 结束时间标志
     */
    public static final String ENDTIMEMARK = "e";

    /**
     * 时间戳格式转换
     */
    public static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * 前台默认格式
     */
    public static final String frontTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * snapshotTime时间戳格式
     */
    public static final String snapshotTimeFormat = "yyyy-MM-dd_HH.mm.ss";

    /**
     * 日期格式
     */
    public static final String dateFormat = "yyyy-MM-dd";

    /**
     * 人脸全景图名称后缀
     */
    public static final String FACE_PANORAMA_SUFFIX = "_FacePanorama.jpg";

    /**
     * 人脸识别抠图名称后缀
     */
    public static final String FACE_PIC_SUFFIX = "_FacePic.jpg";

    /**
     * url分割符
     */
    public static final String URL_SEPARATOR = "/";

    /**
     * 人脸库根目录名称
     */
    public static final String FACE_LIB_ROOT = "faceLibRoot";

    /**
     * 人脸信息图名称后缀
     */
    public static final String FACE_Image_SUFFIX = "_Faceimage.jpg";

    /**
     *
     * 用户头像后缀
     */
    public static final String HEAD_SUFFIX = "_Head.jpg";

    /**
     * 查询锁状态超时时间，单位秒
     */
    public static final int QUERY_LOCK_STATUS_OUTTIME = 2;

    /**
     * 人脸门禁授权前缀
     */
    public static final String FaceAccCtrl = "faceAccCtrl.";


}
