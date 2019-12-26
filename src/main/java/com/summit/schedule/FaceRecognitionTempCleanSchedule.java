package com.summit.schedule;


import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import com.summit.MainAction;
import com.summit.entity.FaceRecognitionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

/**
 * 人脸扫描缓存图片定时清理调度器
 */
@Slf4j
@Component
public class FaceRecognitionTempCleanSchedule {

    @Autowired
    RedisTemplate<String, Object> genericRedisTemplate;

    private String tempRootPath;

    public FaceRecognitionTempCleanSchedule() {
        tempRootPath = new StringBuilder()
                .append(SystemUtil.getUserInfo().getCurrentDir())
                .append(File.separator)
                .append(MainAction.SnapshotFileName)
                .append(File.separator)
                .append(MainAction.FaceRecognitionFileName)
                .toString();
    }

    @Scheduled(fixedDelay = 2000)
    public void cleanFaceRecognitionTemp() {
        if(!FileUtil.exist(tempRootPath)){
            return;
        }
        File[] fileList = FileUtil.ls(tempRootPath);

        for (File file : fileList) {
            boolean isExist = isExistFaceRecognitionTempFile(file.getAbsolutePath());
            if (!isExist) {
                FileUtil.del(file);
            }
        }

    }

    private boolean isExistFaceRecognitionTempFile(String absolutePath) {
        Set<String> keyList = genericRedisTemplate.keys(MainAction.FACE_AUTH_CACHE_PREFIX + "*");
        for (String key : keyList) {
            FaceRecognitionInfo faceRecognitionInfo = (FaceRecognitionInfo) genericRedisTemplate.opsForValue().get(key);
            if (absolutePath.equals(faceRecognitionInfo.getFaceImagePath())) {
                return true;
            }
        }
        return false;
    }
}
