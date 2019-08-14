package com.summit.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class MybatisPage<T> extends Page<T> {

    public MybatisPage(){}

    public MybatisPage(long current, long size){
        super(current,size);
    }

}
