package com.example.decathlon.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

	public List<String[]> read(String filename) throws Exception {
		List<String[]> rows = new ArrayList<>();

		FileInputStream fis = new FileInputStream(filename);
		Workbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		for (Row row : sheet) {
			String[] cells = new String[3];
			for (int i = 0; i < 3; i++) {
				if (row.getCell(i) == null) {
					cells[i] = "";
				} else {
					cells[i] = row.getCell(i).toString();
				}
			}
			rows.add(cells);
		}

		workbook.close();
		fis.close();

		return rows;
	}
}