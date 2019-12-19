package com.summit.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class FaceRecognitionAspect {


    @Before(value = "@annotation(FaceRecognition)")
    public void before(JoinPoint joinPoint){
        System.out.println("[Aspect1] before advise");
    }
}
