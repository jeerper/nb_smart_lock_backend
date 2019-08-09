package com.summit.util;

import com.summit.dao.entity.LockInfo;
import com.summit.dao.entity.Page;

import java.util.List;

public class PageConverter {

    public static void convertPage(Page page) {
        Integer current;
        Integer pageSize;
        if(page == null || (pageSize = page.getPageSize()) == null || (current = page.getCurrent()) == null){
            return;
        }
        if(current < 1 || pageSize < 0){
            //若current 或 pageSize不合法，则只取第一条记录
            page.setCurrent(0);
            page.setPageSize(1);
            return;
        }
        page.setCurrent((current - 1) * pageSize);
    }

}
