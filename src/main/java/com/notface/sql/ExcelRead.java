package com.notface.sql;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelRead {

	public static Object[][] getData(File file) {
		Object[][] data = new Object[0][];
		try {
			InputStream inp = new FileInputStream(file);

			// 获取工作簿
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			int lastRowNum = sheet.getLastRowNum();
			int lastCellNum = row.getLastCellNum();

			data = new Object[lastRowNum][lastCellNum];

			for (int i = 0; i < lastRowNum; i++) {
				row = sheet.getRow(i);
				for (int j = 0; j < lastCellNum; j++) {
					Cell cell = row.getCell(j);
					cell.setCellType(CellType.STRING);
					data[i][j] = cell;
				}
			}
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}