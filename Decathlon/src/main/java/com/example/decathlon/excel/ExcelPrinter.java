package com.example.decathlon.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelPrinter {

	public void print(List<String[]> rows, String filename) throws Exception {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Results");

		int rowNum = 0;
		int maxColumns = 0;

		for (String[] rowData : rows) {
			Row row = sheet.createRow(rowNum++);
			maxColumns = Math.max(maxColumns, rowData.length);
			int col = 0;
			for (String cell : rowData) {
				row.createCell(col++).setCellValue(cell);
			}
		}

		for (int i = 0; i < maxColumns; i++) {
			sheet.autoSizeColumn(i);
		}

		FileOutputStream out = new FileOutputStream(filename);
		workbook.write(out);
		out.close();
		workbook.close();
	}
}