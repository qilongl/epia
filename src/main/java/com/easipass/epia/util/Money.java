package com.easipass.epia.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 *
 * @version 1.0
 */

public class Money {
    public Money() {
    }

    public static String getChnmoney(String strNum) {

        double d = NumberHelper.string2Double(strNum);
        //大写数字的数组
        String as[] = {
                "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"
        };
        //定义大写货币单位的排列数组
        String as1[] = {
                "仟", "佰", "拾", "万", "仟", "佰", "拾", "亿", "仟", "佰",
                "拾", "万", "仟", "佰", "拾", "元", "角", "分"
        };

        String as2[] = new String[40];
        String as3[] = new String[40];
        double d1 = 0.0D;
        int ai[] = new int[20];
        double d4 = 1000000000000000D;
        StringBuffer stringbuffer = new StringBuffer("");
        String s;
        //判断待转换金额是否等于零
        if (d == 0.0D) {
            s = "零元整";
        } else {
            //将小数点删除
            d *= 100D;
            //获得d4的数量级，即d所在的数量级（如：d为1200098.0,d4为1000000）
            for (; d4 >= 1.0D; d4 /= 10D) {
                double d2 = d / d4;
                if (d2 >= 1.0D)
                    break;
            }

            int i;
            //将d值按位进行截取并存放到数组中
            for (i = 0; d4 >= 1.0D; i++) {
                //获得d整除后的值，如1.200098
                double d3 = d / d4;
                //截取整数并放到数组中，如1，2,0,....
                ai[i] = (int) d3;
                //取d余数，如200098，00098,...
                d %= d4;
                //整除，将d4的数量级减小，如：10000，1000，100,...
                d4 /= 10D;
            }
            //获得数值的长度
            int i1 = i;
            //转换后大写金额的长度
            int k = i1 * 2 - 1;
            //货币单位数组的长度
            int l = 17;
            for (i = i1 - 1; i >= 0; i--) {
                //将货币单位加到数组中，从数组中的最后一个值开始，如：分，角，...
                as2[k] = as1[l];
                k--;
                //将金额转换成大写并放到数组中，如，玖，捌，零，......
                as2[k] = as[ai[i]];
                k--;
                l--;
            }

            i1 *= 2;
            k = 0;
            l = 0;
            for (i = 0; i < i1; i++) {
                //判断数组中是否有零的值
                if (as2[i].compareTo("零") == 0) {
                    //判断是否到数组的最后一位
                    if (as2[i + 1] != null) {
                        //判断零后的字符是否为万，亿，元三个字符
                        if (as2[i + 1].compareTo("万") != 0 && as2[i + 1].compareTo("亿") != 0 && as2[i + 1].compareTo("元") != 0) {
                            as3[k] = as2[i];
                            i++;
                            k++;
                        } else {
                            i++;
                            as3[k] = as2[i];
                            k++;
                        }
                    }
                } else {
                    //生成新的数组，如，壹万捌千元整
                    as3[k] = as2[i];
                    k++;
                }
            }


            for (k = 0; as3[k] != null; k++) {
                //将金额数组中的零值替换为"_"
                if (as3[k].compareTo("零") == 0 && as3[k + 1] != null && (as3[k + 1].compareTo("万") == 0 || as3[k + 1].compareTo("亿") == 0 || as3[k + 1].compareTo("元") == 0 || as3[k + 1].compareTo("零") == 0))
                    as3[k] = "_";
            }

            //将金额数组中的倒数第二个的零值替换为"_"
            if (as3[k - 1].compareTo("零") == 0)
                as3[k - 1] = "_";

            k = 0;
            i = 0;

            for (; as3[k] != null; k++) {
                //将数组中的"_"删除
                if (as3[k].compareTo("_") != 0) {
                    as2[i] = as3[k];
                    i++;
                }
            }

            i1 = i;
            String ss = "";
            //生成金额转换后的大写字符串
            for (int j = 0; j < i1; j++) {
                stringbuffer.append(as2[j]);
                if (as2[j].compareTo("亿") == 0 && as2[j + 1].compareTo("万") == 0)
                    as2[j + 1] = "";
            }
            if (stringbuffer.indexOf("角") == -1 && stringbuffer.indexOf("分") == -1) {
                s = stringbuffer.append("整").toString();
            } else {
                s = stringbuffer.toString();
            }
        }
        return s;
    }
}


