package com.summit.sdk.huawei.model;

public enum  FaceZipUploadStatus {
    FaceNoDetected(0,"上传的图片中没有检测到人脸,上传失败"),
    FaceRepeat(1,"人脸添加失败,头像重复,上传失败"),
    FaceSimilar(2,"发现人脸库中有相似的人脸,上传失败"),
    FaceUploading(3,"上传中"),
    FaceUploadSucess(4,"导入成功");
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
