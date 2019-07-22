package com.summit.sdk.huawei.callback;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.summit.sdk.huawei.HWPuSDKLibrary;
import com.summit.sdk.huawei.PU_META_DATA;
import com.summit.sdk.huawei.PU_UserData;
import com.summit.sdk.huawei.model.CardType;
import com.summit.sdk.huawei.model.FaceInfo;
import com.summit.sdk.huawei.model.FaceLibType;
import com.summit.sdk.huawei.model.Gender;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Slf4j
public class RealDataCallBack implements HWPuSDKLibrary.pfRealDataCallBack {

    private ClientFaceInfoCallback clientFaceInfoCallback;

    public RealDataCallBack(ClientFaceInfoCallback clientFaceInfoCallback) {
        this.clientFaceInfoCallback = clientFaceInfoCallback;

    }

    @Override
    public void apply(Pointer szBuffer, NativeLong lSize, Pointer pUsrData) {
        FaceInfo faceInfo = null;
        faceInfo = procBuffer(szBuffer, lSize, HWPuSDKLibrary.LAYER_TWO_TYPE.COMMON, faceInfo);
        faceInfo = procBuffer(szBuffer, lSize, HWPuSDKLibrary.LAYER_TWO_TYPE.TARGET, faceInfo);
        if (faceInfo != null) {
            if (clientFaceInfoCallback != null) {
                faceInfo.setDeviceIp(pUsrData.getString(0));
                Observable.just(faceInfo)
                        .observeOn(Schedulers.io())
                        .subscribe(new Action1<FaceInfo>() {
                            @Override
                            public void call(FaceInfo faceInfo) {
                                clientFaceInfoCallback.invoke(faceInfo);
                            }
                        });
            }
        }
    }

    private FaceInfo procBuffer(Pointer szBuffer, NativeLong lSize, int layerTwoType, FaceInfo faceInfo) {
        PointerByReference pu_meta_data_pointer_pointer = new PointerByReference(Pointer.NULL);
        HWPuSDKLibrary.INSTANCE.IVS_User_GetMetaData(szBuffer, lSize, layerTwoType, pu_meta_data_pointer_pointer);
        faceInfo = procMetaData(pu_meta_data_pointer_pointer, faceInfo);
        HWPuSDKLibrary.INSTANCE.IVS_User_FreeMetaData(pu_meta_data_pointer_pointer);
        return faceInfo;
    }

    private FaceInfo procMetaData(PointerByReference pu_meta_data_pointer_pointer, FaceInfo faceInfo) {
        PU_META_DATA data = Structure.newInstance(PU_META_DATA.class, pu_meta_data_pointer_pointer.getValue());
        data.read();
        PU_UserData[] userData = (PU_UserData[]) data.pstMetaUserData.toArray(data.usValidNumber);
        for (PU_UserData userDataEntity : userData) {
            switch (userDataEntity.eType) {
                //人脸匹配率
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_MATCHRATE:
                    log.debug("================人脸信息业务处理=================");
                    if (faceInfo == null) {
                        faceInfo = new FaceInfo();
                    }
                    float matchRate = userDataEntity.unMetaData.IntValue / 100f;
                    faceInfo.setFaceMatchRate(matchRate);

                    log.debug("人脸匹配率:{}%", faceInfo.getFaceMatchRate());
                    break;
                //人脸信息,对应摄像头的人脸库信息
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_INFO:
                    if (faceInfo == null) {
                        break;
                    }
                    faceInfo.setName(StrUtil.str(userDataEntity.unMetaData.stFaceInfo.name, "").trim());
                    faceInfo.setGender(Gender.codeOf(userDataEntity.unMetaData.stFaceInfo.iGender));
                    faceInfo.setBirthday(StrUtil.str(userDataEntity.unMetaData.stFaceInfo.birthday, "").trim());
                    faceInfo.setProvince(StrUtil.str(userDataEntity.unMetaData.stFaceInfo.province, "").trim());
                    faceInfo.setCity(StrUtil.str(userDataEntity.unMetaData.stFaceInfo.city, "").trim());
                    faceInfo.setCardType(CardType.codeOf(userDataEntity.unMetaData.stFaceInfo.iCardType));
                    faceInfo.setCardId(StrUtil.str(userDataEntity.unMetaData.stFaceInfo.cardID, "").trim());

                    log.debug("名字:" + faceInfo.getName());
                    log.debug("性别:{}", faceInfo.getGender().getGenderDescription());
                    log.debug("生日:" + faceInfo.getBirthday());
                    log.debug("省级:" + faceInfo.getProvince());
                    log.debug("地市:" + faceInfo.getCity());
                    log.debug("证件类型:{}", faceInfo.getCardType().getCardTypeDescription());
                    log.debug("证件号:" + faceInfo.getCardId());
                    break;
                //人脸特征属性
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_FEATURE:
                    break;
                //人体特征属性
                case HWPuSDKLibrary.LAYER_THREE_TYPE.HUMAN_FEATURE:
                    break;
                //名单库名字
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_LIB_NAME:
                    if (faceInfo == null) {
                        break;
                    }
                    byte[] faceLibNameBytes = userDataEntity.unMetaData.stBinay.pBinaryData.getByteArray(0,
                            userDataEntity.unMetaData.stBinay.ulBinaryLenth.intValue());
                    faceInfo.setFaceLibName(StrUtil.str(faceLibNameBytes, "").trim());
                    log.debug("名单库名称:{}", faceInfo.getFaceLibName());
                    break;
                //名单库类型
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_LIB_TYPE:
                    if (faceInfo == null) {
                        break;
                    }
                    faceInfo.setFaceLibType(FaceLibType.codeOf(userDataEntity.unMetaData.uIntValue));
                    log.debug("名单库类型:{}", faceInfo.getFaceLibType().getFaceLibTypeDescription());
                    break;
                //人脸识别全景图
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_PANORAMA:
                    if (faceInfo == null) {
                        break;
                    }
                    byte[] facePanoramaBytes = userDataEntity.unMetaData.stBinay.pBinaryData.getByteArray(0,
                            userDataEntity.unMetaData.stBinay.ulBinaryLenth.intValue());

                    faceInfo.setFacePanorama(facePanoramaBytes);

                    log.debug("人脸识别全景图长度:{}", faceInfo.getFacePanorama().length);
                    break;
                //人脸识别抠图
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_PIC:
                    if (faceInfo == null) {
                        break;
                    }
                    byte[] facePicBytes = userDataEntity.unMetaData.stBinay.pBinaryData.getByteArray(0,
                            userDataEntity.unMetaData.stBinay.ulBinaryLenth.intValue());
                    faceInfo.setFacePic(facePicBytes);
                    log.debug("人脸识别抠图长度:{}", faceInfo.getFacePic().length);
                    break;
                //人脸识别和人脸库中匹配的图片
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_MATCH:
                    if (faceInfo == null) {
                        break;
                    }
                    byte[] faceMatchBytes = userDataEntity.unMetaData.stBinay.pBinaryData.getByteArray(0,
                            userDataEntity.unMetaData.stBinay.ulBinaryLenth.intValue());
                    faceInfo.setFaceMatch(faceMatchBytes);
                    log.debug("人脸识别和人脸库中匹配的图片长度:{}", faceInfo.getFaceMatch().length);
                    break;
                //抓拍时间
                case HWPuSDKLibrary.LAYER_THREE_TYPE.PIC_SNAPSHOT_TIME:
                    if (faceInfo == null) {
                        break;
                    }
                    DateTime time = new DateTime(userDataEntity.unMetaData.IntValue * 1000L);
                    faceInfo.setPicSnapshotTime(time.toString("yyyy-MM-dd HH:mm:ss"));
                    log.debug("抓怕时间:" + faceInfo.getPicSnapshotTime());
                    break;
                //名单库中的人脸ID，用来维持特征 record的一致性
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACELIB_RECORDID:
                    break;
                //相机通道号
                case HWPuSDKLibrary.LAYER_THREE_TYPE.CHANNEL_ID:
                    break;
                //人脸位置(实时位置框)
                case HWPuSDKLibrary.LAYER_THREE_TYPE.FACE_POS:
                    break;
                //target类型，当前用于区分人脸后处理抠图和人脸识别以及人脸识别多机协同
                case HWPuSDKLibrary.LAYER_THREE_TYPE.TARGET_TYPE:
                    break;
                //车辆类型
                case HWPuSDKLibrary.LAYER_THREE_TYPE.VEHICLE_TYPE:
                    break;
                //C50车辆类型
                case HWPuSDKLibrary.LAYER_THREE_TYPE.VEHICLE_TYPE_EXT:
                    break;
                //人体位置(实时位置框)
                case HWPuSDKLibrary.LAYER_THREE_TYPE.HUMAN_RECT:
                    break;
                default:
//                    log.debug("未知数据类型eType-Hex: 0x" + Convert.toHex(Convert.intToBytes(userDataEntity.eType)).toUpperCase());
                    break;
            }
        }
        return faceInfo;
    }
}
