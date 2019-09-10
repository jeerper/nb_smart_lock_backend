package com.summit.sdk.huawei.model;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.Data;

@Data
public class DeviceInfo {
    private Pointer deviceIpPointer;
    private NativeLong ulIdentifyId;
    private NativeLong  ulChannelId;

    public DeviceInfo(Pointer deviceIpPointer, NativeLong ulIdentifyId, NativeLong ulChannelId) {
        this.deviceIpPointer = deviceIpPointer;
        this.ulIdentifyId = ulIdentifyId;
        this.ulChannelId = ulChannelId;
    }

    public DeviceInfo(Pointer deviceIpPointer, NativeLong ulIdentifyId) {
        this.deviceIpPointer = deviceIpPointer;
        this.ulIdentifyId = ulIdentifyId;
    }

    public String getDeviceIp() {
        return this.deviceIpPointer.getString(0);
    }

    public Pointer getDeviceIpPointer() {
        return deviceIpPointer;
    }

    public void setDeviceIpPointer(Pointer deviceIpPointer) {
        this.deviceIpPointer = deviceIpPointer;
    }

    public NativeLong getUlIdentifyId() {
        return ulIdentifyId;
    }

    public void setUlIdentifyId(NativeLong ulIdentifyId) {
        this.ulIdentifyId = ulIdentifyId;
    }
}
