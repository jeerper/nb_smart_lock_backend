package com.summit.entity;

import lombok.Data;

/**
 * 人脸识别信息
 */
@Data
public class FaceRecognitionInfo {
    private String faceId;
    private String faceImagePath;
}
