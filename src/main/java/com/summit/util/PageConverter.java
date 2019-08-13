package com.summit.util;

import com.summit.dao.entity.SimplePage;

public class PageConverter {

    public static void convertPage(SimplePage page) {
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

    public static int getPageCount(int pageSize, int rowsCount) {
        if(pageSize <= 0){
            pageSize = 1;
        }
        return rowsCount % pageSize == 0 ? rowsCount / pageSize : rowsCount / pageSize + 1;
    }

}
