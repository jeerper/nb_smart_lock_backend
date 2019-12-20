package com.summit.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summit.MainAction;
import com.summit.common.constant.CommonConstant;
import com.summit.common.entity.ResponseCodeEnum;
import com.summit.common.util.ResultBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FaceRecognitionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    RedisTemplate<String, Object> genericRedisTemplate;
    @Autowired
    JwtSettings jwtSettings;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader(CommonConstant.REQ_HEADER);
        if(StrUtil.isBlank(token)){
            response.setCharacterEncoding(CommonConstant.UTF8);
            response.setContentType(CommonConstant.CONTENT_TYPE);
            response.setStatus(HttpStatus.OK.value());
            PrintWriter printWriter = response.getWriter();
            printWriter.append(objectMapper.writeValueAsString(ResultBuilder.buildError(ResponseCodeEnum.CODE_4007)));
            return false;
        }
        boolean isContains= token.startsWith(CommonConstant.TOKEN_SPLIT);
        token=token.replace(CommonConstant.TOKEN_SPLIT, "");
        BoundValueOperations<String,Object> boundValueOperations=genericRedisTemplate.boundValueOps(MainAction.FACE_AUTH_CACHE_PREFIX +token);
        String cacheToken=(String)boundValueOperations.get();
        if(StrUtil.isBlank(cacheToken)||(!isContains)){
            response.setCharacterEncoding(CommonConstant.UTF8);
            response.setContentType(CommonConstant.CONTENT_TYPE);
            response.setStatus(HttpStatus.OK.value());
            PrintWriter printWriter = response.getWriter();
            printWriter.append(objectMapper.writeValueAsString(ResultBuilder.buildError(ResponseCodeEnum.CODE_4008)));
            return false;
        }

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSettings.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
        String faceId=(String)claims.get(MainAction.FACE_ID);
        FaceInfoContextHolder.setFaceId(faceId);
        boundValueOperations.expire(jwtSettings.getExpireLength(), TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        FaceInfoContextHolder.clear();
    }


}
