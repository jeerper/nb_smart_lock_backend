package com.summit.controller;

import com.summit.common.entity.RestfulEntityBySummit;
import com.summit.common.entity.UserInfo;
import com.summit.common.web.filter.UserContextHolder;
import com.summit.service.SafeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SafeReportController {

    @Autowired
    private SafeReportService safeReportService;

    public RestfulEntityBySummit getSafeReport(){

        UserInfo uerInfo = UserContextHolder.getUserInfo();
        String[] roles = uerInfo.getRoles();

        return null;
    }

}
