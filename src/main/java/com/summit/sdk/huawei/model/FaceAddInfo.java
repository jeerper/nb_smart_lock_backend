package com.summit.sdk.huawei.model;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * Created by Administrator on 2019/9/2.
 */
public class FaceAddInfo {
    private Pointer faceIpPointer;
    private NativeLong ulIdentifyId;

    public FaceAddInfo(Pointer faceIpPointer, NativeLong ulIdentifyId) {
        this.faceIpPointer = faceIpPointer;
        this.ulIdentifyId = ulIdentifyId;
    }

    public Pointer getFaceIpPointer() {
        return faceIpPointer;
    }

    public void setFaceIpPointer(Pointer faceIpPointer) {
        this.faceIpPointer = faceIpPointer;
    }

    public NativeLong getUlIdentifyId() {
        return ulIdentifyId;
    }

    public void setUlIdentifyId(NativeLong ulIdentifyId) {
        this.ulIdentifyId = ulIdentifyId;
    }
}
