package com.summit.sdk.huawei.model;

public class FaceInfo {
    /**
     * 名字
     */
    private String name;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 省级
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 证件类型
     */
    private CardType cardType;
    /**
     * 证件ID
     */
    private String cardId;
    /**
     * 人脸匹配率
     */
    private float faceMatchRate;
    /**
     * 人脸库名称
     */
    private String faceLibName;
    /**
     * 人脸库类型
     */
    private FaceLibType faceLibType;
    /**
     * 人脸识别全景图
     */
    private byte[] facePanorama;
    /**
     * 人脸识别抠图
     */
    private byte[] facePic;
    /**
     * 人脸识别和人脸库中匹配的图片
     */
    private byte[] faceMatch;
    /**
     * 抓拍时间
     */
    private String picSnapshotTime;
    /**
     * 设备IP
     */
    private String deviceIp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public float getFaceMatchRate() {
        return faceMatchRate;
    }

    public void setFaceMatchRate(float faceMatchRate) {
        this.faceMatchRate = faceMatchRate;
    }

    public String getFaceLibName() {
        return faceLibName;
    }

    public void setFaceLibName(String faceLibName) {
        this.faceLibName = faceLibName;
    }

    public FaceLibType getFaceLibType() {
        return faceLibType;
    }

    public void setFaceLibType(FaceLibType faceLibType) {
        this.faceLibType = faceLibType;
    }

    public byte[] getFacePanorama() {
        return facePanorama;
    }

    public void setFacePanorama(byte[] facePanorama) {
        this.facePanorama = facePanorama;
    }

    public byte[] getFacePic() {
        return facePic;
    }

    public void setFacePic(byte[] facePic) {
        this.facePic = facePic;
    }

    public byte[] getFaceMatch() {
        return faceMatch;
    }

    public void setFaceMatch(byte[] faceMatch) {
        this.faceMatch = faceMatch;
    }

    public String getPicSnapshotTime() {
        return picSnapshotTime;
    }

    public void setPicSnapshotTime(String picSnapshotTime) {
        this.picSnapshotTime = picSnapshotTime;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
}
