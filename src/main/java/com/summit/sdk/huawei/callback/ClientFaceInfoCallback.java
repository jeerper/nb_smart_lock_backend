package com.summit.sdk.huawei.callback;

import com.summit.dao.entity.AccCtrlProcess;
import com.summit.sdk.huawei.model.CameraUploadType;

public interface ClientFaceInfoCallback {

    void invoke(Object object);

    void insertData(CameraUploadType type, AccCtrlProcess accCtrlProcess) throws Exception;
}
