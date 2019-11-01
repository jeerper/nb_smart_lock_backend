package com.summit.redis.face;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.constants.CommonConstants;
import com.summit.entity.SimFaceInfoAccCtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FaceAccCtrlCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(FaceAccCtrlCache.class);

    @Autowired
    RedisTemplate<String, String> redisTemplateWithUserInfo;
    @Autowired
    ObjectMapper objectMapper;
    /**
     * 设置授权进度信息缓存
     * @param accessControlId 门禁id
     * @param simFaceInfoAccCtl 授权进度信息
     */
    public void  setFaceAccCtrl(String accessControlId, SimFaceInfoAccCtl simFaceInfoAccCtl){
        try {
            String simFaceInfoAccCtlString = objectMapper.writeValueAsString(simFaceInfoAccCtl);
            LOGGER.debug("设置授权进度信息缓存:{}:{}", accessControlId, simFaceInfoAccCtlString);
            redisTemplateWithUserInfo.opsForValue().set(accessControlId,simFaceInfoAccCtlString);
        } catch (Exception e) {
            LOGGER.error("设置授权进度信息缓存失败", e);
        }
    }
    /**
     * 获取授权进度信息缓存
     * @param accessControlId 门禁id
     * @return
     */
    public SimFaceInfoAccCtl getFaceAccCtrl(String accessControlId) {
        try {
            String simFaceInfoAccCtlString =
                    redisTemplateWithUserInfo.opsForValue().get(accessControlId);
            LOGGER.debug("读取授权进度信息缓存:{}:{}", accessControlId, simFaceInfoAccCtlString);
            if (StrUtil.isBlank(simFaceInfoAccCtlString)) {
                return null;
            }
            return objectMapper.readValue(simFaceInfoAccCtlString, SimFaceInfoAccCtl.class);
        } catch (Exception e) {
            LOGGER.error("获取授权进度信息缓存失败", e);
            return null;
        }
    }

    /**
     * 授权进度信息缓存
     *
     * @param accessControlId 门禁id
     * @return
     */
    public boolean delSimFaceInfoAccCtl(String accessControlId) {
        boolean result = redisTemplateWithUserInfo.delete(accessControlId);
        LOGGER.debug("设置门禁授权过期时间:{}:{}", accessControlId, result);
        return true;
    }

    /**
     * 设置过期时间 60秒
     * @param accessControlId
     */
    public void setSimFaceInfoAccCtrlTime(String accessControlId){
        Boolean result = redisTemplateWithUserInfo.expire(CommonConstants.FaceAccCtrl + accessControlId, 60, TimeUnit.SECONDS);
        LOGGER.debug("设置门禁授权过期时间:{}:{}", accessControlId, result);
    }
}
