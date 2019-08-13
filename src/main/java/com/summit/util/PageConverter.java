package com.summit.util;

import com.summit.cbb.utils.page.Pageable;
import com.summit.dao.entity.SimplePage;

public class PageConverter {

    /**
     * 前端传入分页和数据库limit转换
     * @param page 简单分页对象，其中含有页大小和当前页
     */
    public static void convertPage(SimplePage page) {
        if(page == null)
            return;
        Integer current = page.getCurrent();
        Integer pageSize = page.getPageSize();
        //若当前页和页大小都为null，说明无分页
        if(current == null && pageSize == null){
            return;
        }
        //若当前页和页大小只有一个为null，参数不合法
        if(current == null || pageSize == null){
            page.setCurrent(0);
            page.setPageSize(0);
            return;
        }
        //若current 或 pageSize不合法，则查不到记录
        if(current < 1 || pageSize < 0){
            page.setCurrent(0);
            page.setPageSize(0);
            return;
        }
        page.setCurrent((current - 1) * pageSize);
    }

    /**
     * 获取总页数
     * @param pageSize 页大小
     * @param rowsCount 总条数
     * @return 总页数
     */
    private static int getPageCount(Integer pageSize, Integer rowsCount) {
        //页大小和总条数有一个为空，说明只有一页
        if(rowsCount == null || pageSize == null){
            return 1;
        }
        //若rowsCount 或 pageSize不合法，则总页数为0
        if(rowsCount < 0 || pageSize <= 0){
            return 0;
        }
        return rowsCount % pageSize == 0 ? rowsCount / pageSize : rowsCount / pageSize + 1;
    }

    /**
     * 获取当前页记录数
     * @param page 简单分页对象，其中含有页大小和当前页
     * @param rowsCount 所有记录数
     * @return 当前页记录数
     */
    private static int getPageRowsCount(SimplePage page, int rowsCount) {
        //page为null，说明无分页，查询全部，当前页总数为所有记录数
        if(page == null)
            return rowsCount;
        Integer current = page.getCurrent();
        Integer pageSize = page.getPageSize();
        //若当前页和页大小都为null，说明无分页，查询全部，当前页总数为所有记录数
        if(current == null && pageSize == null){
            return rowsCount;
        }
        //若当前页和页大小只有一个为null，参数不合法，当前页总数为0
        if(current == null || pageSize == null){
            return 0;
        }
        //若current 或 pageSize不合法，则查不到记录
        if(current < 1 || pageSize < 0){
            return 0;
        }
        return current * pageSize > rowsCount ? rowsCount % pageSize : pageSize;
    }

    /**
     * 获取完整分页对象Pageable
     * @param page 简单分页对象，其中含有页大小和当前页
     * @param rowsCount 总记录数
     * @return 完整分页对象Pageable
     */
    public static Pageable getPageable(SimplePage page, Integer rowsCount){
        Pageable pageable = new Pageable();
        if(page == null){
            pageable.setPageCount(1);
            pageable.setRowsCount(rowsCount);
            pageable.setPageRowsCount(rowsCount);
            return pageable;
        }
        Integer current = page.getCurrent();
        Integer pageSize = page.getPageSize();
        //若当前页和页大小都为null，说明无分页
        if(current == null && pageSize == null){
            pageable.setPageCount(1);
            pageable.setRowsCount(rowsCount);
            pageable.setPageRowsCount(rowsCount);
            return pageable;
        }
        //若当前页和页大小只有一个为null，参数不合法
        if(current == null || pageSize == null){
            pageable.setPageCount(1);
            pageable.setRowsCount(0);
            pageable.setPageRowsCount(0);
            return pageable;
        }
        //current 或 pageSize不合法
        if(current < 1 || pageSize < 0){
            pageable.setPageCount(1);
            pageable.setRowsCount(0);
            pageable.setPageRowsCount(0);
            return pageable;
        }
        if(rowsCount == null || rowsCount < 0)
            rowsCount = 0;

        pageable.setCurPage(page.getCurrent());
        pageSize = page.getPageSize();
        pageable.setPageSize(pageSize);
        pageable.setRowsCount(rowsCount);
        pageable.setPageCount(getPageCount(pageSize, rowsCount));
        pageable.setPageRowsCount(getPageRowsCount(page, rowsCount));

        return pageable;
    }

}
