package com.easipass.epia.util;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lql on 2016/6/21.
 */
public class HtmlParserUtil {
    private static final String en_code="UTF-8";
    /**
     * 解析字符串
     *
     * @param inputHTML String
     * @return Parser
     */
    public static Parser createParser(String inputHTML) throws Exception
    {
        Lexer mLexer = new Lexer(new Page(inputHTML));
        Parser parser=new Parser(mLexer,
                new DefaultParserFeedback(DefaultParserFeedback.QUIET));
        parser.setEncoding(en_code);
        return parser;
    }

    /**
     * 通过属性解析html
     *
     * @param content   解析正文
     * @param tagName   标签名称
     * @param attrName  属性名称
     * @param attrValue 属性值
     * @return
     * @throws Exception
     */
    public static NodeList getNodeListByAttr(String content, String tagName, String attrName, String attrValue) throws Exception {
        Parser parser = createParser(content);
        NodeFilter table = new TagNameFilter(tagName);
        NodeFilter datalistClass = new HasAttributeFilter(attrName, attrValue);
        NodeFilter basictableFilter = new AndFilter(datalistClass, table);
        NodeList divlist = parser.extractAllNodesThatMatch(basictableFilter);
        return divlist;
    }

    /**
     * 通过标签名解析html
     * @param content
     * @param tagName1
     * @param tagName2
     * @return
     * @throws Exception
     */
    public static NodeList getNodeListByOrTagName(String content, String tagName1, String tagName2) throws Exception {
        Parser parser = new Parser(content);
        NodeFilter th = new TagNameFilter(tagName1);
        NodeFilter td = new TagNameFilter(tagName2);
        OrFilter orFilter = new OrFilter(th, td);
        NodeList tdthList = parser.extractAllNodesThatMatch(orFilter);
        return tdthList;
    }

    /**
     * 解析出包含有 tagname1,tagname2,tagname3,tagname4的标签集合
     * @param content 传入的string正文
     * @param tagName1
     * @param tagName2
     * @param tagName3
     * @param tagName4
     * @return
     * @throws Exception
     */
    public static NodeList getNodeListByOrTagName(String content, String tagName1, String tagName2, String tagName3, String tagName4)throws Exception
    {
        Parser parser = new Parser(content);
        NodeFilter t1 = new TagNameFilter(tagName1);
        NodeFilter t2 = new TagNameFilter(tagName2);
        NodeFilter t3 = new TagNameFilter(tagName3);
        NodeFilter t4 = new TagNameFilter(tagName4);
        OrFilter t1_t2_orFilter = new OrFilter(t1, t2);
        OrFilter t3_t4_orFilter = new OrFilter(t3, t4);
        OrFilter orFilter = new OrFilter(t1_t2_orFilter, t3_t4_orFilter);
        NodeList list = parser.extractAllNodesThatMatch(orFilter);
        return list;
    }
    /**
     * and 过滤器
     * @param content
     * @param tagName1
     * @param tagName2
     * @return
     * @throws Exception
     */
    public static NodeList getNodeListByAndTagName(String content, String tagName1, String tagName2) throws Exception {
        Parser parser = new Parser(content);
        NodeFilter th = new TagNameFilter(tagName1);
        NodeFilter td = new TagNameFilter(tagName2);
        AndFilter andFilter = new AndFilter(th, td);
        NodeList tdthList = parser.extractAllNodesThatMatch(andFilter);
        return tdthList;
    }

    /**
     * 通过标签名称来解析html
     * @param content
     * @param tagName
     * @return
     * @throws Exception
     */
    public static NodeList getNodeListByTagName(String content, String tagName) throws Exception {
        Parser parser = new Parser(content);
        NodeFilter td = new TagNameFilter(tagName);
        NodeList tdthList = parser.extractAllNodesThatMatch(td);
        return tdthList;
    }

    /**
     * 从正文content 中，以 startContent 为起点，endContent 为重点，截取中间的字符串作为结果返回
     * @param content
     * @param startContent
     * @param endContent
     * @return
     * @throws Exception
     */
    public static String getValue(String content, String startContent, String endContent)
    {
        int start=0;
        int end=0;
        if(null==startContent)
            start=0;
        else
            start=content.indexOf(startContent)+startContent.length();
        content=content.substring(start);
        if(null==endContent)
            end=content.length();
        else
            end=content.indexOf(endContent);
        content=content.substring(0,end);
        return content;
    }

    /**
     * 从 str 中，以 startContent 为开始，endContent为结束，把中间的字符串替换成newContent字符串
     * @param str
     * @param startContent
     * @param endContent
     * @param newContent
     * @return
     */
    public static StringBuffer replace(StringBuffer str,String startContent,String endContent,String newContent)
    {
        int start=0;
        int end=0;
        start=str.indexOf(startContent);
        String newStr=str.substring(start,str.length());
        end=newStr.indexOf(endContent)+endContent.length();
        return str.replace(start,start+end,newContent);
    }
    /**
     * 输入值过滤，祛除无效字符
     * @param inputValue
     * @return
     * @throws Exception
     */
    public static List<String> invalidList=null;
    static {
        invalidList=new ArrayList();
        invalidList.add("&nbsp;");
        invalidList.add("\n");
        invalidList.add("\t");
        invalidList.add(" ");
    }

    /**
     * 清理掉无效字符串
     * @param inputValue
     * @return
     * @throws Exception
     */
    public static String filterValue(String inputValue) {
        if(inputValue==null)return null;
        for (int i = 0; i < invalidList.size(); i++)
        {
            String invalidStr=invalidList.get(i);
            if(inputValue.contains(invalidStr))
            {
                inputValue=inputValue.replaceAll(invalidStr,"");
            }
        }
        return inputValue;
    }

    /**
     * 通过标签、属性、值，过滤出包含正文的节点
     * @param content
     * @param tag
     * @param attrKey
     * @param attrValue
     * @param targetContent
     * @return
     * @throws Exception
     */
    public static Node getNodeByAttrAndContent(String content, String tag, String attrKey, String attrValue, String targetContent)throws Exception
    {
        NodeList nodeList= HtmlParserUtil.getNodeListByAttr(content,tag,attrKey,attrValue);
        Node targetNode=null;
        for(int i=0;i<nodeList.size();i++)
        {
            Node divNode=nodeList.elementAt(i);
            if(divNode.toHtml().contains(targetContent))
            {
                targetNode=divNode;
                break;
            }
        }
        return targetNode;
    }

    /**
     * 转换成UTF8
     * @param content
     * @return
     * @throws Exception
     */
    public static String toUTF8(String content)throws Exception
    {
        return URLEncoder.encode(content,"utf-8");
    }

    /**
     * 转换成GBK
     * @param content
     * @return
     * @throws Exception
     */
    public static String toGBK(String content)throws Exception
    {
        return URLEncoder.encode(content,"gbk");
    }
    public static void main(String args[])throws Exception
    {
        String str="djfkj    dkfj dkjfkd&nbsp;fjkdjf    kdjfkd\ndjfkdjfkdjkfd\tfdjkfjdk";
        str=filterValue(str);
        System.out.println(str);

        String sql="select * from report where id=#{reports.reportid} and name={{{${{{username}}}}";
        StringBuffer name= HtmlParserUtil.replace(new StringBuffer(sql),"${","}","?");
        System.out.println(name);
    }



}
