package com.easipass.epia.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by StrangeDragon on 2019/4/24 16:44
 **/
public class Test1 {
    private String name;
    private String address;
    private int age;

    private static Logger logger = LoggerFactory.getLogger(Test1.class);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            logger.info("info:{}","我擦嘞");
            Class<?> cls = Class.forName("com.easipass.epia.test.Test1");
            Object obj = cls.newInstance();
            for (Method method : cls.getMethods()) {
                System.out.println(method.getName());
            }
            Method method = cls.getMethod("test", String.class, String.class);
            Object result = method.invoke(obj, "刘奇龙", "1231");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String test(String str) {
        System.out.println(str);
        return str;
    }

    public String test(String str, String str1) {
        System.out.println("test(String str,String str1)");
        return str;
    }


}
