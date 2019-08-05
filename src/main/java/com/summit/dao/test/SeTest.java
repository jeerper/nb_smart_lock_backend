package com.summit.dao.test;

import io.swagger.models.auth.In;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeTest {

    @Test
    public void test(){
        Object obj = new Object();
        aaa(obj);

        System.out.println(obj);
    }
    @Test
    public void test3(){
        String[] strs = {"1", "3", "5", "4"};
        List<String> ints = Arrays.asList(strs);
        List<String> subList = ints.subList(0, 2);
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
