package com.easipass.epia.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lql on 2017/9/12.
 */
public class WriteExcelUtil {

    private static Logger logger = LoggerFactory.getLogger(WriteExcelUtil.class);

    public static void main(String[] args) {
        String path = "f://temp//demo.xlsx";
        String name = "test";
        List<String> titles = new ArrayList();
        titles.add("id");
        titles.add("name");
        titles.add("age");
        titles.add("birthday");
        titles.add("gender");
        titles.add("date");
        List<Map<String, Object>> values = new ArrayList();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap();
            map.put("id", i + 1D);
            map.put("name", "test_" + i);
            map.put("age", i * 1.5);
            map.put("gender", "man");
            map.put("birthday", new Date());
            map.put("date", Calendar.getInstance());
            values.add(map);
        }
        System.out.println(writerExcel(path, "xlsx", name, titles, values));
    }

    /**
     * 数据写入Excel文件
     *
     * @param path   文件路径，包含文件全名，例如：D://file//demo.xls
     * @param name   sheet名称
     * @param titles 行标题列
     * @param values 数据集合，key为标题，value为数据
     * @return True\False
     */
    public static boolean writerExcel(String path, String style, String name, List<String> titles, List<Map<String, Object>> values) {
        logger.info("path : {}", path);
        return generateWorkbook(path, name, style, titles, values);
    }

    /**
     * 将数据写入指定path下的Excel文件中
     *
     * @param path   文件存储路径
     * @param name   sheet名
     * @param style  Excel类型
     * @param titles 标题串
     * @param values 内容集
     * @return True\False
     */
    private static boolean generateWorkbook(String path, String name, String style, List<String> titles, List<Map<String, Object>> values) {
        logger.info("file style : {}", style);
        Workbook workbook;
        if ("XLS".equals(style.toUpperCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }
        // 生成一个表格
        Sheet sheet;
        if (null == name || "".equals(name)) {
            sheet = workbook.createSheet(); // name 为空则使用默认值
        } else {
            sheet = workbook.createSheet(name);
        }
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成样式
        Map<String, CellStyle> styles = createStyles(workbook);
        /*
         * 创建标题行
         */
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        Map<String, Integer> titleOrder = new HashMap();
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String title = titles.get(i);
            cell.setCellValue(title);
            titleOrder.put(title, i);
        }
        /*
         * 写入正文
         */
        Iterator<Map<String, Object>> iterator = values.iterator();
        int index = 0; // 行号
        while (iterator.hasNext()) {
            index++; // 出去标题行，从第一行开始写
            row = sheet.createRow(index);
            Map<String, Object> value = iterator.next();
            for (Map.Entry<String, Object> map : value.entrySet()) {
                // 获取列名
                String title = map.getKey();
                // 根据列名获取序号
                int i = titleOrder.get(title);
                // 在指定序号处创建cell
                Cell cell = row.createCell(i);
                // 设置cell的样式
                if (index % 2 == 1) {
                    cell.setCellStyle(styles.get("cellA"));
                } else {
                    cell.setCellStyle(styles.get("cellB"));
                }
                // 获取列的值
                Object object = map.getValue();
                // 判断object的类型
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (object instanceof Double) {
                    cell.setCellValue((Double) object);
                } else if (object instanceof Date) {
                    String time = simpleDateFormat.format((Date) object);
                    cell.setCellValue(time);
                } else if (object instanceof Calendar || object instanceof Date) {
                    Calendar calendar = (Calendar) object;
                    String time = simpleDateFormat.format(calendar.getTime());
                    cell.setCellValue(time);
                } else if (object instanceof Boolean) {
                    cell.setCellValue((Boolean) object);
                } else if (object instanceof String) {
                    cell.setCellValue(object.toString());
                } else {
                    cell.setCellValue(object==null?"":object+"");
                }
            }
        }
        /*
         * 写入到文件中
         */
        boolean isCorrect = false;
        try {
            File file = new File(path);
            OutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            isCorrect = true;
        } catch (IOException e) {
            isCorrect = false;
            logger.error("write Excel file error : {}", e.getMessage());
        }
        try {
            workbook.close();
        } catch (IOException e) {
            isCorrect = false;
            logger.error("workbook closed error : {}", e.getMessage());
        }
        return isCorrect;
    }

    /**
     * Create a library of cell styles
     */
    /**
     * @param wb
     * @return
     */
    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap();
        // 标题样式
        CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐
        titleStyle.setLocked(true); // 样式锁定
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 12);
        titleFont.setBold(true);
        titleFont.setFontName("微软雅黑");
        titleStyle.setFont(titleFont);
        styles.put("title", titleStyle);

        // 文件头样式
//        CellStyle headerStyle = wb.createCellStyle();
//        headerStyle.setAlignment(HorizontalAlignment.CENTER);
//        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // 前景色
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 颜色填充方式
//        headerStyle.setWrapText(true);
//        headerStyle.setBorderRight(BorderStyle.THIN); // 设置边界
//        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderLeft(BorderStyle.THIN);
//        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderTop(BorderStyle.THIN);
//        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        headerStyle.setBorderBottom(BorderStyle.THIN);
//        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        Font headerFont = wb.createFont();
//        headerFont.setFontHeightInPoints((short) 12);
//        headerFont.setColor(IndexedColors.WHITE.getIndex());
//        titleFont.setFontName("微软雅黑");
//        titleFont.setBold(true);
//        headerStyle.setFont(headerFont);
//        styles.put("header", headerStyle);

//        Font cellStyleFont = wb.createFont();
//        cellStyleFont.setFontHeightInPoints((short) 12);
//        cellStyleFont.setColor(IndexedColors.BLUE_GREY.getIndex());
//        cellStyleFont.setFontName("微软雅黑");
//
//        // 正文样式A
//        CellStyle cellStyleA = wb.createCellStyle();
//        cellStyleA.setAlignment(HorizontalAlignment.CENTER); // 居中设置
//        cellStyleA.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyleA.setWrapText(true);
//        cellStyleA.setBorderRight(BorderStyle.THIN);
//        cellStyleA.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderLeft(BorderStyle.THIN);
//        cellStyleA.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderTop(BorderStyle.THIN);
//        cellStyleA.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setBorderBottom(BorderStyle.THIN);
//        cellStyleA.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleA.setFont(cellStyleFont);
//        styles.put("cellA", cellStyleA);
//
//        // 正文样式B:添加前景色为浅黄色
//        CellStyle cellStyleB = wb.createCellStyle();
//        cellStyleB.setAlignment(HorizontalAlignment.CENTER);
//        cellStyleB.setVerticalAlignment(VerticalAlignment.CENTER);
//        cellStyleB.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
//        cellStyleB.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyleB.setWrapText(true);
//        cellStyleB.setBorderRight(BorderStyle.THIN);
//        cellStyleB.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderLeft(BorderStyle.THIN);
//        cellStyleB.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderTop(BorderStyle.THIN);
//        cellStyleB.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setBorderBottom(BorderStyle.THIN);
//        cellStyleB.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyleB.setFont(cellStyleFont);
//        styles.put("cellB", cellStyleB);

        return styles;
    }
}
