package com.easipass.epia.util;

import com.alibaba.fastjson.serializer.ValueFilter;

/**
 * @Author lql
 * @Date 2018/6/13 18:28
 */
public class JsonValueFilter {
    public final static int JSON_OBJECT = 0;
    public final static int JSON_ARRAY = 1;

    //空值过滤为""
    public static ValueFilter changeNullToString() {
        ValueFilter filter = new ValueFilter() {
            @Override
            public Object process(Object obj, String s, Object v) {
                if (v == null)
                    return "";
                return v;
            }
        };
        return filter;
    }

    public static int getJsonType(String str) {
        if (str.startsWith("{"))
            return 0;
        if (str.startsWith("["))
            return 1;
        return 2;
    }
}
