package com.summit.sdk.huawei.model;

/**
 * Created by Administrator on 2019/9/4.
 */
public class JNAGlobalVariant {
    public static final int PU_DOMAIN_LEN = 256;             //域名最大长度
    public static final int PU_DIO_NAME_LEN = 36;                 //开关量名称长度
    public static final int PU_ALARM_CAMERA_MAX = 4; //联动镜头信息列表
    public static final int PU_ALARM_DO_MAX = 4;    //联动开关量输出ID
    public static final int PU_LPR_CAPTURE_RES = 200;    //车牌抓拍结果
    public static final int PU_MANUAL_LPR_CAPTURE_RES = 201;    //手动车牌抓拍结果
    // 密钥长度
    public static final int PU_CRYPTION_PASSWD_LEN = 44;

    public static final int MAX_POINT_NUM = 10;

    // 802.1x EAP-TLS证书文件最大路径
    public static final int PU_CERT_FILE_PATH_MAX = 512;

    // Pwd最大长度
    public static final int PU_PASSWORD_LEN = 64;

    //告警矩形最大值
    public static final int PU_ALARM_AREA_MAX = 16;

    //告警时间段最大值
    public static final int PU_ALARM_TIME_MAX = 8;
    //
    public static final int PU_RESERVE_LEN = 32;
    public static final String pathCrypt = "715AO1FEC11AD58A";

}
