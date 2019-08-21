package com.summit.dao.test;

import com.summit.constants.CommonConstants;
import io.swagger.models.auth.In;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SeTest {

    @Test
    public void test(){
        Object obj = new Object();
        aaa(obj);

        System.out.println(obj);
    }
    @Test
    public void testxx(){
        if(null instanceof String){
            System.out.println("未空指针");
        }


    }
    @Test
    public void testDate() throws ParseException {
        String time = "2019-08-16T05:55:59.689Z";
        Date parse = CommonConstants.frontTimeFormat.parse(time);
        System.out.println(parse);
    }
    @Test
    public void test4(){
        Object obj = null;
        bbb(obj);

        System.out.println(obj);
    }
    void bbb(Object obj){
        obj = new Object();
    }
    @Test
    public void test3(){
        String[] strs = {"1", "3", "5", "4"};
        List<String> ints = Arrays.asList(strs);
        //从下标到下标而不是从下标开始多少长度
        List<String> subList = ints.subList(1, 2);
        System.out.println(subList);
    }

    @Test
    public void test2(){
        Object obj = new Object();
        Object obj2 = new Object();
        List<Object> objs = new ArrayList<>();
        objs.add(obj);
        objs.add(obj2);
        System.out.println(objs);
        bbb(objs);
        System.out.println(objs);
    }

    void bbb(List<Object> objs){
        objs.remove(1);
    }
    void aaa(Object obj){
        obj = null;
    }
}
