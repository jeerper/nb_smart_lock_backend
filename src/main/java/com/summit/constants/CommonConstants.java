package com.summit.constants;

import java.text.SimpleDateFormat;

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
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 前台默认格式
     */
    public static final SimpleDateFormat frontTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    /**
     * snapshotTime时间戳格式转换
     */
    public static final SimpleDateFormat snapshotTimeFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * 日期格式转换
     */
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

}
