package com.easipass.epia.util;

import com.alibaba.fastjson.JSON;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lql on 2017/8/4.
 */
public class StringHelper {
    /**
     * 判断字符串非空
     *
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj) {
        if (obj == null)
            return false;
        String str = obj.toString();
        if ("".equals(str) || str.trim().equals("") || "null".equals(str))
            return false;
        return true;
    }

    /**
     * 检测字符串是否为空
     */
    public static boolean strIsNull(String str) {
        return ((str == null) || "".equals(str));
    }

    /**
     * object对象转成string
     *
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    /**
     * 得到32位字符串
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 从响应的字符串中反序列化出ResponseResult对象
     *
     * @param rs
     * @return
     * @throws Exception
     */
    public static ResponseResult getRRFromStream(String rs) throws Exception {
        ResponseResult rr = null;
        Object o2 = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(rs));
        ObjectInputStream ois = new ObjectInputStream(bis);
        o2 = ois.readObject();
        bis.close();
        ois.close();
        rr = (ResponseResult) o2;
        return rr;
    }

    public static void main(String[] args) throws Exception {
        ResponseResult rr1 = new ResponseResult();
        ResponseResult rr2 = new ResponseResult();
        rr1.setMsg("Hello World!");
        rr1.setResult(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(rr1);
        oos.flush();
        oos.close();
        baos.close();
        byte[] bytes = baos.toByteArray();
        String encodeString = new BASE64Encoder().encode(bytes);
        System.out.println(encodeString);
        //-----------------------------------------------------

        byte[] result2 = new BASE64Decoder().decodeBuffer(encodeString);
        ByteArrayInputStream bais = new ByteArrayInputStream(result2);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object = ois.readObject();
        bais.close();
        ois.close();
        rr2 = (ResponseResult) object;
        System.out.println(rr2.toString());
        System.out.println(JSON.toJSONString(rr2));

    }

    /**
     *
     */
    public static String null2String(String strIn) {
        return strIn == null ? "" : strIn;
    }

    public static Map string2Map(String strIn, String strDim) {
        Map map = new HashMap();
        List list = string2ArrayList(strIn, strDim);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            map.put(str.substring(0, str.indexOf("=")).trim(), str.substring(str.indexOf("=") + 1).trim());
        }
        return map;
    }

    /**
     *
     */
    public static String null2String(Object strIn) {
        return strIn == null ? "" : "" + strIn.toString().trim();
    }

    public static String null2String(Object strIn, String defstr) {
        return strIn == null ? defstr : "" + strIn;
    }

    public static String GBK2ISO(String s) {
        try {
            return new String(s.getBytes("GBK"), "ISO8859_1");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static String ISO2GBK(String s) {
        try {
            return new String(s.getBytes("ISO8859_1"), "GBK");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static String ISO2UTF(String s) {
        try {
            return new String(s.getBytes("ISO8859_1"), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static ArrayList string2ArrayList2(String strIn, String strDim) {
        strIn = null2String(strIn);
        strDim = null2String(strDim);
        ArrayList strList = new ArrayList();

        String[] sr2 = strIn.split(strDim);
        for (int i = 0; i < sr2.length; i++) {
            strList.add(null2String(sr2[i]));
        }
        if (strIn.endsWith(strDim))
            strList.add("");

        return strList;
    }

    /**
     *
     */
    public static ArrayList<String> string2ArrayList(String strIn, String strDim) {
        return string2ArrayList(strIn, strDim, false);
    }

    /**
     *
     */
    public static HashSet<String> string2Set(String strIn, String strDim) {
        ArrayList<String> list = string2ArrayList(strIn, ",");
        HashSet<String> set = new HashSet<String>();
        set.addAll(list);//给set填充
        list.clear();//清空list，不然下次把set元素加入此list的时候是在原来的基础上追加元素的
        list.addAll(set);//把set的
        return set;
    }


    /**
     *
     */
    public static ArrayList<String> string2ArrayList(String strIn, String strDim,
                                                     boolean bReturndim) {
        strIn = null2String(strIn);
        strDim = null2String(strDim);
        ArrayList strList = new ArrayList();
        StringTokenizer strtoken = new StringTokenizer(strIn, strDim,
                bReturndim);
        while (strtoken.hasMoreTokens()) {
            strList.add(strtoken.nextToken());
        }
        return strList;
    }

    /**
     *
     */
    public static String[] string2Array(String strIn, String strDim) {
        return string2Array(strIn, strDim, false);
    }

    /**
     *
     */
    public static String[] string2Array(String strIn, String strDim,
                                        boolean bReturndim) {
        ArrayList strlist = string2ArrayList(strIn, strDim, bReturndim);
        int strcount = strlist.size();
        String[] strarray = new String[strcount];
        for (int i = 0; i < strcount; i++) {
            strarray[i] = (String) strlist.get(i);
        }
        return strarray;
    }

    /**
     *
     */
    public static boolean contains(Object a[], Object s) {
        if (a == null || s == null) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != null && a[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public static String replaceChar(String s, char c1, char c2) {
        if (s == null) {
            return s;
        }

        char buf[] = s.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == c1) {
                buf[i] = c2;
            }
        }
        return String.valueOf(buf);
    }

    /**
     *
     */
    // public static String replaceString(String sou, String s1, String s2) {
    // int idx = sou.indexOf(s1);
    // if (idx < 0) {
    // return sou;
    // }
    // return replaceString(sou.substring(0, idx) + s2 +
    // sou.substring(idx + s1.length()), s1, s2);
    //
    // }

    /**
     * 替换字符串
     *
     * @param strSource
     * @param strFrom
     */
    public static String replaceString(String strSource, String strFrom,
                                       String strTo) {
        if (strSource == null) {
            return strSource;
        }
        int i = 0;
        if ((i = strSource.indexOf(strFrom, i)) >= 0 && strTo != null) {
            char[] cSrc = strSource.toCharArray();
            char[] cTo = strTo.toCharArray();
            int len = strFrom.length();
            StringBuffer buf = new StringBuffer(cSrc.length);
            buf.append(cSrc, 0, i).append(cTo);
            i += len;
            int j = i;
            while ((i = strSource.indexOf(strFrom, i)) > 0) {
                buf.append(cSrc, j, i - j).append(cTo);
                i += len;
                j = i;
            }
            buf.append(cSrc, j, cSrc.length - j);
            return buf.toString();
        }
        return strSource;
    }

    public static String passwordBuilder(int length)        //随机生成动态密码
    {
        String str = "";
        String[] arr =
                {
                        "2", "3", "4", "5", "6", "7", "8", "9",
                        "a", "d", "e", "f", "g", "h", "i", "j",
                        "m", "n", "r", "t", "u", "y",
                        "A", "B", "D", "E", "F", "G", "H", "J",
                        "L", "M", "N", "Q", "R", "T", "Y"
                };

        Random rd = new Random();
        while (str.length() < length) {

            String temp = arr[rd.nextInt(37)];
            if (str.indexOf(temp) == -1) {
                str += temp;
            }
        }
        return str;
    }

    /**
     *
     *
     */
    public static String replaceStringFirst(String sou, String s1, String s2) {
        int idx = sou.indexOf(s1);
        if (idx < 0) {
            return sou;
        }
        return sou.substring(0, idx) + s2 + sou.substring(idx + s1.length());
    }

    /**
     *
     *
     */
    public static String replaceRange(String sentence, String oStart,
                                      String oEnd, String rWord, boolean matchCase) {
        int sIndex = -1;
        int eIndex = -1;
        if (matchCase) {
            sIndex = sentence.indexOf(oStart);
        } else {
            sIndex = sentence.toLowerCase().indexOf(oStart.toLowerCase());
        }
        if (sIndex == -1 || sentence == null || oStart == null || oEnd == null
                || rWord == null) {
            return sentence;
        } else {
            if (matchCase) {
                eIndex = sentence.indexOf(oEnd, sIndex);
            } else {
                eIndex = sentence.toLowerCase().indexOf(oEnd.toLowerCase(),
                        sIndex);
            }
            String newStr = null;
            if (eIndex > -1) {
                newStr = sentence.substring(0, sIndex) + rWord
                        + sentence.substring(eIndex + oEnd.length());
            } else {
                newStr = sentence.substring(0, sIndex) + rWord
                        + sentence.substring(sIndex + oStart.length());
            }
            return replaceRange(newStr, oStart, oEnd, rWord, matchCase);
        }
    }

    // Empty checks
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if a String is empty ("") or null.
     * </p>
     * <p>
     * <pre>
     *  StringUtils.isEmpty(null)      = true
     *  StringUtils.isEmpty(&quot;&quot;)        = true
     *  StringUtils.isEmpty(&quot; &quot;)       = false
     *  StringUtils.isEmpty(&quot;bob&quot;)     = false
     *  StringUtils.isEmpty(&quot;  bob  &quot;) = false
     * </pre>
     * <p>
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * String. That functionality is available in isBlank().
     * </p>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.equalsIgnoreCase("null") || str.trim().length() == 0;
    }

    //判断是否是表单的ID
    public static boolean isID(String str) {
        return
                !isEmpty(str) &&
                        str.trim().indexOf(" ") <= -1 && //  不含空格
                        str.trim().indexOf("'") <= -1 && //   不含单引号, 防注入
                        (
                                //业务需求
                                (str.trim().indexOf("N") == 0 && str.trim().length() == 6 &&
                                        NumberHelper.string2Int(StringHelper.null2String(str.trim().substring(1))) != -1)
                                        || str.trim().indexOf("_RptType") > 0
                                        || str.trim().indexOf("_STOCKFORECAST") > 0
                                        || str.trim().indexOf("_T_USERCOMPANY") > 0
                                        || str.trim().indexOf("_RptType") > 0
                                        || NumberHelper.string2Int(StringHelper.null2String(str.trim())) != -1
                                        || str.trim().indexOf(".") > 0
                                        //系统需求
                                        || str.trim().length() >= 32
                        );
        //return true;
    }


    /**
     * <p>
     * Removes control characters (char &lt;= 32) from both ends of this String
     * returning <code>null</code> if the String is empty ("") after the trim
     * or if it is <code>null</code>.
     * <p>
     * <p>
     * The String is trimmed using {@link String#trim()}. Trim removes start
     * and end characters &lt;= 32. To strip whitespace use
     * {@link #(String)}.
     * </p>
     * <p>
     * <pre>
     *  StringUtils.trimToNull(null)          = null
     *  StringUtils.trimToNull(&quot;&quot;)            = null
     *  StringUtils.trimToNull(&quot;     &quot;)       = null
     *  StringUtils.trimToNull(&quot;abc&quot;)         = &quot;abc&quot;
     *  StringUtils.trimToNull(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, <code>null</code> if only chars &lt;= 32,
     * empty or null String input
     * @since 2.0
     */
    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    /**
     * <p>
     * Removes control characters (char &lt;= 32) from both ends of this String,
     * handling <code>null</code> by returning <code>null</code>.
     * </p>
     * <p>
     * <p>
     * The String is trimmed using {@link String#trim()}. Trim removes start
     * and end characters &lt;= 32. To strip whitespace use
     * {@link #(String)}.
     * </p>
     * <p>
     * <p>
     * To trim your choice of characters, use the {@link #(String, String)}
     * methods.
     * </p>
     * <p>
     * <pre>
     *  StringUtils.trim(null)          = null
     *  StringUtils.trim(&quot;&quot;)            = &quot;&quot;
     *  StringUtils.trim(&quot;     &quot;)       = &quot;&quot;
     *  StringUtils.trim(&quot;abc&quot;)         = &quot;abc&quot;
     *  StringUtils.trim(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static boolean parseBoolean(String param) {
        if (isEmpty(param)) {
            return false;
        }
        switch (param.charAt(0)) {
            case '1':
            case 'y':
            case 'Y':
            case 't':
            case 'T':
                return true;
        }
        return false;
    }

    public static String getRandomStr(int length) {
        String psd = "";
        char c;
        int i;
        int isnum = 0;
        for (int j = 0; j < length; j++) {
            if (isnum == 0) {
                isnum = 1;
                c = (char) (Math.random() * 26 + 'a');
                psd += c;
            } else {
                isnum = 0;
                c = (char) (Math.random() * 10 + '0');
                psd += c;
            }
        }
        return psd;
    }

    public static String fromDB(String s) {
        char c[] = s.toCharArray();
        char ch;
        int i = 0;
        StringBuffer buf = new StringBuffer();

        while (i < c.length) {
            ch = c[i++];
            if (ch == '\"') {
                buf.append("\\\"");
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    public static String Array2String(Object[] strIn, String strDim) {
        StringBuffer str = new StringBuffer();
        int len = strIn.length;
        for (int i = 0; i < len; i++) {
            str.append((i == 0) ? strIn[i] : strDim + strIn[i]);
        }
        return str.toString();
    }

    public static String Array2String(String[] strIn, String strDim) {
        StringBuffer strOut = new StringBuffer();
        for (String tempStr : strIn) {
            strOut.append(tempStr).append(strDim);
        }
        if (strOut.length() > 0)
            strOut.delete(strOut.lastIndexOf(strDim), strOut.length());
        return strOut.toString();
    }

    public static String ArrayList2String(ArrayList strIn, String strDim) {
        return List2String(strIn, strDim);
    }

    public static String List2String(List strIn, String strDim) {
        StringBuffer strOut = new StringBuffer();
        for (Object o : strIn) {
            strOut.append(o.toString()).append(strDim);
        }
        if (strOut.length() > 0)
            strOut.delete(strOut.lastIndexOf(strDim), strOut.length());
        return strOut.toString();
    }

    public static String fillValuesToString(String str, Hashtable ht) {
        return fillValuesToString(str, ht, '$');
    }

    public static String fillValuesToString(String str, Hashtable ht,
                                            char VARIABLE_PREFIX) {

        char TERMINATOR = '\\';

        if (str == null || str.length() == 0 || ht == null) {
            return str;
        }

        char s[] = str.toCharArray();
        char ch, i = 0;
        String vname;
        StringBuffer buf = new StringBuffer();

        ch = s[i];
        while (true) {
            if (ch == VARIABLE_PREFIX) {
                vname = "";
                if (++i < s.length) {
                    ch = s[i];
                } else {
                    break;
                }
                while (true) {
                    if (ch != '_' && ch != '-'
                            && !Character.isLetterOrDigit(ch)) {
                        break;
                    }
                    vname += ch;
                    if (++i < s.length) {
                        ch = s[i];
                    } else {
                        break;
                    }
                }

                if (vname.length() != 0) {
                    String vval = (String) ht.get(vname);
                    if (vval != null) {
                        buf.append(vval);
                    }
                }
                if (vname.length() != 0 && ch == VARIABLE_PREFIX) {
                    continue;
                }
                if (ch == TERMINATOR) {
                    if (++i < s.length) {
                        ch = s[i];
                    } else {
                        break;
                    }
                    continue;
                }
                if (i >= s.length) {
                    break;
                }
            }

            buf.append(ch);
            if (++i < s.length) {
                ch = s[i];
            } else {
                break;
            }
        }
        return buf.toString();
    }

    public static String formatMutiIDs(String ids) {
        String ret = "";
        ArrayList arrayids = string2ArrayList(ids, ",");
        for (int i = 0; i < arrayids.size(); i++) {
            String _id = null2String(String.valueOf(arrayids.get(i))).trim();
            if (StringHelper.isEmpty(_id)) continue;
            //剔除ids中的重复id
            if (ret.contains(_id)) continue;
            if (isID(_id)) {
                ret += ",'" + _id + "'";
            }
        }
        if (!ret.equals(""))
            return ret.substring(1);
        else
            return "''";
    }

    /**
     * 得到PreparedStatement执行的SQL语句中的in字符串
     *
     * @author pitt
     * @date 20140211
     */

    public static String getPreparedStatementStr(String ids) {
        String ret = "";
        ArrayList arrayids = string2ArrayList(ids, ",");
        for (int i = 0; i < arrayids.size(); i++) {
            String _id = null2String(String.valueOf(arrayids.get(i))).trim();
            if (StringHelper.isEmpty(_id)) continue;
            if (isID(_id)) {
                ret += ",?";
            }
        }
        if (!ret.equals(""))
            return ret.substring(1);
        else
            return "''";
    }

    /**
     * 从aids中剔除bids中的所有id
     */
    public static String removeIDs(String aids, String bids) {
        ArrayList arrayids = string2ArrayList(bids, ",");
        for (int i = 0; i < arrayids.size(); i++) {
            String _id = null2String(String.valueOf(arrayids.get(i))).trim();
            if (isID(_id)) {
                aids = aids.replaceAll(_id, "");
            }
        }
        return formatMutiIDs(aids);
    }

    public static String lift(String arg, int length) {
        int sLength = arg.trim().length();
        if (length < 1 || length > sLength) {
            return arg.trim();
        } else {
            return arg.trim().substring(0, length);
        }

    }

    public static String getDecodeStr(String strIn) {
        if (strIn == null)
            return "";
        String strTemp = "";
        for (int i = 0; i < strIn.length(); i++) {
            char charTemp = strIn.charAt(i);
            switch (charTemp) {
                case 124: // '~'
                    String strTemp2 = strIn.substring(i + 1, i + 3);
                    strTemp = strTemp + (char) Integer.parseInt(strTemp2, 16);
                    i += 2;
                    break;

                case 94: // '^'
                    String strTemp3 = strIn.substring(i + 1, i + 5);
                    strTemp = strTemp + (char) Integer.parseInt(strTemp3, 16);
                    i += 4;
                    break;

                default:
                    strTemp = strTemp + charTemp;
                    break;
            }
        }

        return strTemp;
    }

    /**
     * @param str
     * @return
     */
    public static String getBase64Encode(String str, String prefix) {
        if (isEmpty(prefix)) prefix = "e_n_c_o_d_e";
        try {
            if (!StringHelper.isEmpty(str)) {
                str = EncryptHelper.encryptBASE64(str.getBytes());//加密
                str = prefix + str.replaceAll("\\r\\n", "").replaceAll("\\r", "").replaceAll("\\n", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * //支持加密传输后的解密.  by ray 2015-02-09
     *
     * @param str
     * @return
     */
    public static String getBase64Decode(String str, String prefix) {
        return getBase64Decode(str, prefix, false);
    }


    /**
     * 可以选择是否需要对传进来的字符进行强制解密，即使字符串不是一 e_n_c_o_d_e开头
     *
     * @param str
     * @param prefix
     * @param force  强制需要解密
     * @return
     */
    public static String getBase64Decode(String str, String prefix, boolean force) {
        if (isEmpty(prefix)) prefix = "e_n_c_o_d_e";
        if (!StringHelper.isEmpty(str)) {
            if (str.startsWith(prefix) || force) {
                str = str.replaceFirst("e_n_c_o_d_e", "");
                try {
                    str = new String(EncryptHelper.decryptBASE64(str));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static String getEncodeStr(String strIn) {
        if (strIn == null)
            return "";
        try {
            strIn = URLEncoder.encode(strIn.toString(), "utf-8");
            System.out.println("msg=" + strIn);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strIn;
    }

    /*public static String getEncodeStr(String strIn)
    {
        if(strIn == null)
            return "";
        String strOut = "";
        for(int i = 0; i < strIn.length(); i++)
        {
            int iTemp = strIn.charAt(i);
            if(iTemp > 255)
            {
                String strTemp2 = Integer.toString(iTemp, 16);
                for(int iTemp2 = strTemp2.length(); iTemp2 < 4; iTemp2++)
                    strTemp2 = "0" + strTemp2;

                strOut = strOut + "^" + strTemp2;
            } else
            {
                if(iTemp < 48 || iTemp > 57 && iTemp < 65 || iTemp > 90 && iTemp < 97 || iTemp > 122)
                {
                    String strTemp2 = Integer.toString(iTemp, 16);
                    for(int iTemp2 = strTemp2.length(); iTemp2 < 2; iTemp2++)
                        strTemp2 = "0" + strTemp2;

                    strOut = strOut + "|" + strTemp2;
                } else
                {
                    strOut = strOut + strIn.charAt(i);
                }
            }
        }

        return strOut;
    }*/


    public static String getMoneyStr(String src) {
        return Money.getChnmoney(getfloatToString2(src));
    }


    public static String getfloatToString(String value) {
        int index = value.indexOf("E");
        if (index == -1) {
            return value;
        }

        int num = Integer.parseInt(value.substring(index + 1, value.length()));
        value = value.substring(0, index);
        index = value.indexOf(".");
        value = value.substring(0, index) +
                value.substring(index + 1, value.length());
        String number = value;
        if (value.length() <= num) {
            for (int i = 0; i < num - value.length(); i++) {
                number += "0";
            }
        } else {
            number = number.substring(0, num + 1) + "." +
                    number.substring(num + 1) + "0";
        }
        return number;
    }

    public static String getfloatToString2(String value) {    //保留两位小数
        value = getfloatToString(value);
        int index = value.indexOf(".");
        if (index == -1) return value;
        String value1 = value.substring(0, index);
        String value2 = value.substring(index + 1, value.length()) + "00";
        if (Integer.parseInt(value2.substring(0, 2)) == 0) return value1;
        else return value1 + "." + value2.substring(0, 2);
    }

    public static String numberFormat2(String value) {    //保留两位小数
        double num = NumberHelper.string2Double(StringHelper.null2String(value), 0.0);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    public static String numberFormat2(Double value) {    //保留两位小数
        double num = NumberHelper.string2Double(StringHelper.null2String(value), 0.0);
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    public static String numberFormat2(double value) {    //保留两位小数
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    public static String getRequestHost(HttpServletRequest request) {
        if (request == null) {
            return "";
        } else {
            return (null2String(request.getHeader("Host"))).trim();
        }
    }

    // 以 java 实现 escape  unescape
    private final static String[] hex = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F",
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F",
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F",
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
            "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF",
            "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
            "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF",
            "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF",
            "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF",
            "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
    };
    private final static byte[] val = {
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F
    };

    public static String escape(String s) {
        if (isEmpty(s)) return "";
        StringBuffer sbuf = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            int ch = s.charAt(i);
            if (ch == ' ') {                        // space : map to '+'
                sbuf.append('+');
            } else if ('A' <= ch && ch <= 'Z') {    // 'A'..'Z' : as it was
                sbuf.append((char) ch);
            } else if ('a' <= ch && ch <= 'z') {    // 'a'..'z' : as it was
                sbuf.append((char) ch);
            } else if ('0' <= ch && ch <= '9') {    // '0'..'9' : as it was
                sbuf.append((char) ch);
            } else if (ch == '-' || ch == '_'       // unreserved : as it was
                    || ch == '.' || ch == '!'
                    || ch == '~' || ch == '*'
                    || ch == '\'' || ch == '('
                    || ch == ')') {
                sbuf.append((char) ch);
            } else if (ch <= 0x007F) {              // other ASCII : map to %XX
                sbuf.append('%');
                sbuf.append(hex[ch]);
            } else {                                // unicode : map to %uXXXX
                sbuf.append('%');
                sbuf.append('u');
                sbuf.append(hex[(ch >>> 8)]);
                sbuf.append(hex[(0x00FF & ch)]);
            }
        }
        return sbuf.toString();
    }

    public static String unescape(String s) {
        if (isEmpty(s)) return "";
        StringBuffer sbuf = new StringBuffer();
        int i = 0;
        int len = s.length();
        while (i < len) {
            int ch = s.charAt(i);
            if (ch == '+') {                        // + : map to ' '
                sbuf.append(' ');
            } else if ('A' <= ch && ch <= 'Z') {    // 'A'..'Z' : as it was
                sbuf.append((char) ch);
            } else if ('a' <= ch && ch <= 'z') {    // 'a'..'z' : as it was
                sbuf.append((char) ch);
            } else if ('0' <= ch && ch <= '9') {    // '0'..'9' : as it was
                sbuf.append((char) ch);
            } else if (ch == '-' || ch == '_'       // unreserved : as it was
                    || ch == '.' || ch == '!'
                    || ch == '~' || ch == '*'
                    || ch == '\'' || ch == '('
                    || ch == ')') {
                sbuf.append((char) ch);
            } else if (ch == '%') {
                int cint = 0;
                if ('u' != s.charAt(i + 1)) {         // %XX : map to ascii(XX)
                    cint = (cint << 4) | val[s.charAt(i + 1)];
                    cint = (cint << 4) | val[s.charAt(i + 2)];
                    i += 2;
                } else {                            // %uXXXX : map to unicode(XXXX)
                    cint = (cint << 4) | val[s.charAt(i + 2)];
                    cint = (cint << 4) | val[s.charAt(i + 3)];
                    cint = (cint << 4) | val[s.charAt(i + 4)];
                    cint = (cint << 4) | val[s.charAt(i + 5)];
                    i += 5;
                }
                sbuf.append((char) cint);
            }
            i++;
        }
        return sbuf.toString();
    }


    /**
     * 转换第一个字母为大写
     */
    public static String changeFirstLetter(String string) {
        if (string == null) {
            return null;
        } else {
            String c = string.substring(0, 1);
            String d = c.toUpperCase();
            String returnString = string.replaceFirst(c, d);
            return returnString;
        }

    }

    /**
     * Converts some important chars (int) to the corresponding html string
     */
    public static String conv2Html(int i) {
        if (i == '&')
            return "&amp;";
        else if (i == '<')
            return "&lt;";
        else if (i == '>')
            return "&gt;";
        else if (i == '"')
            return "&quot;";
        else
            return "" + (char) i;
    }

    /**
     * Converts a normal string to a html conform string
     */
    public static String conv2Html(String st) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < st.length(); i++) {
            buf.append(conv2Html(st.charAt(i)));
        }
        return buf.toString();
    }

    /**
     * 移除开头，结尾，或中间的逗号
     *
     * @param oldstationids
     * @return
     */
    public static String removeComma(String oldstationids) {
        if (!isEmpty(oldstationids)) {
            if (oldstationids.indexOf(",,") != -1) {
                return oldstationids.replace(",,", ",");
            }
            if (oldstationids.lastIndexOf(",") == oldstationids.length()) {
                oldstationids = oldstationids.substring(0, oldstationids.lastIndexOf(","));
            }
            if (oldstationids.indexOf(",") == 1) {
                return oldstationids.substring(1);
            }
        }
        return oldstationids;
    }

    /**
     * 生成令牌
     *
     * @param request
     * @return
     */
    public static String generateToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        try {
            byte id[] = session.getId().getBytes();
            byte now[] = new Long(System.currentTimeMillis()).toString()
                    .getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id);
            md.update(now);
            return (md.digest().toString());
        } catch (Exception e) {
            return (null);
        }
    }


    /**
     * 将string中的回车、换行、空格转化为html代码
     *
     * @param sStr
     * @return
     */
    public static String convertHtmlString(String sStr) {
        if (sStr == null || sStr.equals("")) {
            return sStr;
        }

        StringBuffer sTmp = new StringBuffer();
        int i = 0;
        while (i <= sStr.length() - 1) {
//			if (sStr.charAt(i) == '\n' || sStr.charAt(i) == '\r') {
            if (sStr.charAt(i) == '\n') {
                sTmp = sTmp.append("<br>");
            } else if (sStr.charAt(i) == ' ') {
                sTmp = sTmp.append("&nbsp;");
            } else {
                sTmp = sTmp.append(sStr.substring(i, i + 1));
            }
            i++;
        }
        return sTmp.toString();
    }

    /**
     * jstr中的JS关键字符转义，以便直接输出为js脚本内容。
     *
     * @param jstr as String
     * @return String //a="a\\bc";转换后 a=\"a\\\\bc\"
     */
    public static String filterJString(String jstr) {
        jstr = jstr.replaceAll("\\\\", "\\\\");
        jstr = jstr.replaceAll("\"", "\\\\\"");
        return jstr;
    }

    public static String filterJString2(String jstr) {
        if (jstr == null)
            return "";
        jstr = jstr.replaceAll("\\\\", "/");
        jstr = jstr.replaceAll("'", "");
        jstr = jstr.replaceAll("\"", "");
        return jstr;
    }

    /**
     * ${var}
     */
    public final static String REGEXP_VAR1 = "\\$\\{[\\w: \\.\\*\\(\\)]+\\}";
    /**
     * {var}
     */
    public final static String REGEXP_VAR2 = "\\{[\\w: \\*\\.\\(\\)]+\\}";
    /**
     * Ognl express variate #var
     */
    public final static String REGEXP_VAR_OGNL = "#[\\w\\u2E80-\\u9FFF]+";

    /**
     * 分析并获取strTemp=sss${var}dddd中的变量名var
     *
     * @param sTemp
     * @param regExp as String //;regExp==null,默认将获取${var}的变量名称var
     * @return List&lt;String&gt;
     */
    public static List<String> parseTemplateVar(String sTemp, String regExp) {
        List<String> fieldList = new ArrayList<String>();
        if (sTemp == null) return fieldList;
        if (regExp == null) regExp = REGEXP_VAR1;
        int pos = 0;
        if (regExp.equalsIgnoreCase(REGEXP_VAR1)) pos = 2;
        else if (regExp.equalsIgnoreCase(REGEXP_VAR2)) pos = 1;

        //求模板内的字段名
        fieldList = new ArrayList<String>();
        Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sTemp);
        String tmp = null;
        if (m.find()) {
            do {
                tmp = sTemp.substring(m.start(), m.end());
                if (pos > 0)
                    tmp = tmp.substring(pos, tmp.length() - 1);
                fieldList.add(tmp);
            } while (m.find());
        }//else System.out.println("no found!");//end if.
        return fieldList;
    }


    /**
     * 过滤字符串中的正则表达式关键词。
     *
     * @param sText as String
     * @return String
     */
    public static String filterRegexpChar(String sText) {
        int size = sText.length();
        StringBuffer s = new StringBuffer();
        char ch = 0;
        for (int i = 0; i < size; i++) {
            ch = sText.charAt(i);
            switch (ch) {
                case '\\':
                case '^':
                case '$':
                case '*':
                case '(':
                case ')':
                case '[':
                case ']':
                case '{':
                case '}':
                case '+':
                case '?':
                case '.':
                    s.append("\\" + ch);
                    break;
                default:
                    s.append(ch);
            }
        }
        return s.toString();
    }

    public static String translateVelocity(String s) {
        s = s.replaceAll("<w:(\\w+)>([^<]*)</w:\\w+>", "#$1$2");
        return s;
    }

    /**
     * 过滤Sql中的\n,\r,'等关键字符
     *
     * @param sql as String
     * @return String
     */
    public static String filterSqlChar(String sql) {
        if (sql.indexOf('\'') > 0) sql = sql.replaceAll("'", "''");
        sql = sql.replaceAll("\\n", "\\\\n");
        sql = sql.replaceAll("\\r", "\\\\r");
        return sql;
    }

    /**
     * 当原字符串位数不足时，用补足字符在字符串前补足指定的位数
     */
    public static String fillString(String str, int length, char c) {
        while (str.length() < length) {
            str += c;
        }
        return str;
    }


    /**
     * 清除字符串中的HTML 标签
     * 从中获取正真显示的文本内容
     * eg!  一般是以 <a > xxxxxxx </  a>
     * 只获取 中间 xxxxxx 部分
     *
     * @param str
     * @return
     */
    public static String clearHtmlTags(String str) {
        if (isEmpty(str)) return "";
        //清楚
        int iEnd = str.indexOf("</");
        if (iEnd > 0) {
            str = str.substring(0, iEnd);
        }
        int iBegin = str.lastIndexOf(">");
        if (iBegin > 0) {
            str = str.substring(iBegin + 1);
        }

        return str;
    }

    /**
     * 截取特定长度字符串，超过部分用省略号替代
     * 如果控制的字符长度小于3 ，直接返回 ...
     * Html标签部分还要保留
     *
     * @param str
     * @param length
     * @return
     */
    public static String subHtmlStringWithEllipsis(String str, Integer length) {

        //清除标签后的内容
        String content = clearHtmlTags(str);

        //是否有Html标签被清除
        boolean hasHtmlTags = content.length() < str.length();

        String subContent = subStringAsChineseLength(content, length);

        if (hasHtmlTags && content.length() != subContent.length()) {//带标签替换
            str = str.replace(">" + content + "</", ">" + subContent + "</");
        } else {//无标签，整体替换
            str = str.replace(content, subContent);
        }

        return str;

    }


    /**
     * 以中文的标准 截取一定长度的字符串
     *
     * @param content
     * @param length
     * @return
     */
    public static String subStringAsChineseLength(String content, Integer length) {

        try {
            char[] _chars = content.toCharArray();
            byte[] _bytes = content.getBytes("GBK");
            //截取
            if (!isEmpty(content)) {
                //以字节方式截取字符串， length 控制的长度为中文长度
                if (length >= 2) {
                    if (_bytes.length == content.length()) {//全英文数字
                        if (_bytes.length > length * 2) {//需要截取
                            content = content.substring(0, length * 2);
                            content += "...";
                        }
                    } else if (_bytes.length == 2 * content.length()) {//全中文
                        if (_bytes.length > length * 2) {//需要截取
                            content = content.substring(0, length);
                            content += "...";
                        }
                    } else {//中、英文数字都有
                        int c = 0;
                        int _charsLength = 0;
                        for (char ch : _chars) {
                            if (c >= length * 2) break;
                            _charsLength++;
                            if (isChinese(ch)) c += 2;
                            else c += 1;
                        }
                        content = new String(_chars, 0, _charsLength);
                        if (_charsLength < _chars.length) content += "...";//需要添加...
                    }

                } else {
                    content = "...";
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return content;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    @Deprecated
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符在字符串中出现的次数
     *
     * @param string
     * @param a
     * @return
     */
    public static int occurTimes(String string, String a) {
        int pos = -2;
        int n = 0;

        while (pos != -1) {
            if (pos == -2) {
                pos = -1;
            }
            pos = string.indexOf(a, pos + 1);
            if (pos != -1) {
                n++;
            }
        }
        return n;
    }

    /**
     * 对特殊内容字符串进行处理，防止数据库查询时注入
     *
     * @param content
     * @return
     */
    public static String decodeSpecialCharsWhenLikeUseBackslash(String content) {
        // 单引号是oracle字符串的边界,oralce中用2个单引号代表1个单引号
        String afterDecode = content.replaceAll("'", "''");
        // 由于使用了/作为ESCAPE的转义特殊字符,所以需要对该字符进行转义
        // 这里的作用是将"a/a"转成"a//a"
        afterDecode = afterDecode.replaceAll("/", "//");
        // 使用转义字符 /,对oracle特殊字符% 进行转义,只作为普通查询字符，不是模糊匹配
        afterDecode = afterDecode.replaceAll("%", "/%");
        // 使用转义字符 /,对oracle特殊字符_ 进行转义,只作为普通查询字符，不是模糊匹配
        afterDecode = afterDecode.replaceAll("_", "/_");
        return afterDecode;
    }
}
