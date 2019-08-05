package com.summit.controller;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.service.SafeReportService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "NB智能锁操作接口")
@RestController
@RequestMapping("/safeReport")
public class SafeReportController {

    @Autowired
    private SafeReportService safeReportService;

    public RestfulEntityBySummit getSafeReport(){

        UserInfo uerInfo = UserContextHolder.getUserInfo();
        String[] roles = uerInfo.getRoles();

        return null;
    }

}
