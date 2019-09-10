package com.summit.sdk.huawei;

import com.summit.sdk.huawei.model.JNAGlobalVariant;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/9/4.
 */
public class PU_CERT_FILE_PATH_PARA_S extends Structure {
    public PU_CERT_FILE_PATH_PARA_S()
    {
        this.setAlignType(ALIGN_NONE);
    }

    public byte[] szCACertFilePath = new byte[JNAGlobalVariant.PU_CERT_FILE_PATH_MAX]; // CA文件路径

    public byte[] szKeyFilePath = new byte[JNAGlobalVariant.PU_CERT_FILE_PATH_MAX]; // 客户端端私钥路径

    public byte[] szCertFilePath = new byte[JNAGlobalVariant.PU_CERT_FILE_PATH_MAX]; // 客户端端证书路径

    public byte[] szKeyPasswd = new byte[JNAGlobalVariant.PU_PASSWORD_LEN + 4]; // 私钥密码

    public byte[] szReserve = new byte[JNAGlobalVariant.PU_RESERVE_LEN];

    public static class ByReference extends PU_CERT_FILE_PATH_PARA_S implements Structure.ByReference
    {
    }

    @Override
    protected List<String> getFieldOrder()
    {
        return Arrays.asList("szCACertFilePath", "szKeyFilePath", "szCertFilePath", "szKeyPasswd", "szReserve");
    }
}
