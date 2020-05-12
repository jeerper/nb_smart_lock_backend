package com.summit.sdk.huawei.model;

public enum  FaceZipUploadStatus {
    FaceNoDetected(0,"没有检测到人脸,上传失败"),
    FaceRepeat(1,"人脸添加失败,头像重复,上传失败"),
    FaceSimilar(2,"发现人脸库中有相似的人脸,上传失败"),
    FaceUploading(3,"上传中"),
    FaceUploadSucess(4,"导入完毕"),
    FaceUploadImageNameWrong(5,"人脸图片命名不对,上传失败"),
    FaceType(6,"人脸类型为空,上传失败"),
    CardType(7,"证件类型为空,上传失败"),
    CardId(8,"证件号为空,上传失败"),
    Gender(9,"性别为空,上传失败"),
    Birthday(10,"生日为空,上传失败"),
    Province(11,"省份为空,上传失败"),
    City(12,"城市为空,上传失败"),
    FaceEndTime(13,"有效期为空或者格式不对,上传失败"),
    DeptNames(14,"所属机构为空或者不匹配,上传失败"),
    UserName(15,"人脸名称为空或最后一行数据为空,上传失败");
    private int code;
    private String description;

    FaceZipUploadStatus(int code, String description) {

        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
