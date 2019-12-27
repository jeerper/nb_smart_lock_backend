package com.summit.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchFaceResult {
    /**
     *人脸ID
     */
    private String faceId;
    /**
     * 匹配率
     */
    private float score;
}
