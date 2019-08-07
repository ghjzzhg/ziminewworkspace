package org.ofbiz.content.content;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileUtil {

	private static Map<String, HSSFCellStyle> styles = new HashMap<String, HSSFCellStyle>(); // 表格样式集

	private static int rowcnt;// 表格行计数

	/**
	 * 创建表格样式
	 */
	private static Map<String, HSSFCellStyle> createStyles(HSSFWorkbook wb) {
		Map<String, HSSFCellStyle> newstyles = new HashMap<String, HSSFCellStyle>(); // 表格样式集
		// ----------------------表头标题样式---------------------------
		HSSFCellStyle title = wb.createCellStyle();
		HSSFFont title_font = wb.createFont();
		title_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体
		title.setFont(title_font);
		title.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		title.setWrapText(true); // 自动换行
		newstyles.put("title_style", title);

		// ------------------------列名称样式----------------------------
		HSSFCellStyle field = wb.createCellStyle();
		HSSFFont field_font = wb.createFont();
		field_font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体
		field.setFont(field_font);
		field.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		field.setFillForegroundColor((short) 44);// 设置颜色
		field.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 颜色填充方式
		field.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 居中
		field.setWrapText(true); // 自动换行
		newstyles.put("field_style", field);

		HSSFCellStyle matter = wb.createCellStyle();
		HSSFFont matter_font = wb.createFont();
		matter.setFont(matter_font);
		matter.setFillForegroundColor(HSSFColor.RED.index);// 设置颜色
		matter.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		matter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 颜色填充方式
		matter.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 居中
		matter.setWrapText(true); // 自动换行
		newstyles.put("matter_style", matter);
		return newstyles;
	}

	/**
	 *
	 * @param filename 文件详细路径
	 * @param list	数据库查询的数据列表
	 * @param fieldnames 将需要查询列名拼接为数组传递
	 * @param conditions 如果有查询条件，增加查询条件列表（中文）
	 * @param values 查询条件的值已数组形式传递
	 * @param listName 数据库列表的Map的键编辑为数组的形式传递
	 * @return
	 */
	public static boolean generateExcel(String filename, List<Map<String, Object>> list, String fieldnames[],
										String conditions[], String values[], String[] listName){
		rowcnt = 0;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			HSSFWorkbook wb = new HSSFWorkbook();
			styles = createStyles(wb);
			HSSFSheet sheet = wb.createSheet();
			if(null != conditions && conditions.length > 0 ){
				setTitle(conditions, values, sheet);// 设置表头
			}
			setFields(fieldnames, sheet);// 设置列名称
			if (list.size() != 0) {
				setValue(list, sheet, listName);
			}
			wb.write(fos);
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * 添加本次结果的查询条件
	 *
	 * @param conditions
	 *            查询条件
	 * @param values
	 *            查询条件对应的值
	 * @param sheet
	 *            表格页sheet对象
	 */
	private static void setTitle(String[] conditions, String[] values, HSSFSheet sheet) {
		HSSFCell cell = null;
		HSSFRow row = sheet.createRow(rowcnt);
		rowcnt++;
		cell = row.createCell(0);
		cell.setCellValue("--查询条件--");
		row = sheet.createRow(rowcnt);
		rowcnt++;
		for (int i = 0; i < conditions.length; i++) {
			sheet.setColumnWidth(i * 2, 13 * 256);// 设置列宽
			cell = row.createCell(i * 2);
			cell.setCellStyle(styles.get("title_style"));// 添加标题样式
			cell.setCellValue(conditions[i]);
			sheet.setColumnWidth(i * 2 + 1, 13 * 256);// 设置列宽
			cell = row.createCell(i * 2 + 1);
			cell.setCellValue(values[i]);
		}
	}

	/**
	 * 设置表格列名称
	 *
	 * @param fieldnames
	 *            列名称
	 * @param sheet
	 *            表格页sheet对象
	 */
	private static void setFields(String[] fieldnames, HSSFSheet sheet) {
		HSSFCell cell = null;
		HSSFRow row = sheet.createRow(rowcnt);
		rowcnt++;
		cell = row.createCell(0);
		cell.setCellValue("--结果明细--");
		row = sheet.createRow(rowcnt);
		rowcnt++;
		for (int i = 0; i < fieldnames.length; i++) {
			sheet.setColumnWidth(i, 13 * 256);// 设置列宽
			cell = row.createCell(i);
			cell.setCellStyle(styles.get("field_style"));// 添加标题样式
			cell.setCellValue(fieldnames[i]);
		}
	}

	/**
	 * 填充表格内容
	 *
	 * @param list
	 *            数据集List<Map<String,Object>>,适应JUMP框架结果集返回格式
	 * @param sheet
	 *            表格页sheet对象
	 */
	private static void setValue(List<Map<String, Object>> list, HSSFSheet sheet,
								 String[] listName) {
		int listsize = list.size();// 行数
		HSSFRow row = null;
		HSSFCell cell = null;
		String obj = null;
		for (int i = 0; i < listsize; i++) {
			row = sheet.createRow(rowcnt);
			rowcnt++;
			for (int j = 0; j < listName.length; j++) {
				cell = row.createCell(j);
				Map map = (Map)list.get(i);// 序号
				obj = map.get(listName[j]).toString();
				cell.setCellValue(obj + "");
			}
		}
	}
}
