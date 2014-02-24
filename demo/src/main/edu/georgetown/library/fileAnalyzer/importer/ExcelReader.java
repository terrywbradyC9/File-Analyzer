package edu.georgetown.library.fileAnalyzer.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {
	public static Vector<Vector<String>> readExcel(File f) throws InvalidFormatException, IOException {		
		Vector<Vector<String>> data = new Vector<Vector<String>>();

		FileInputStream fs = new FileInputStream(f);
		try {
			Workbook w = WorkbookFactory.create(fs);
			if (w.getNumberOfSheets() > 1) throw new InvalidFormatException("Workbook has more than one worksheet");
			if (w.getNumberOfSheets() != 1) throw new InvalidFormatException("Workbook does not have a worksheet");
			Sheet sheet = w.getSheetAt(0);
			for(int r=0; r<sheet.getPhysicalNumberOfRows(); r++) {
				Vector<String> row = new Vector<String>();
				data.add(row);
				Row srow = sheet.getRow(r);
				if (srow == null) continue;
				for(int c=0; c<srow.getLastCellNum(); c++) {
					Cell cell = srow.getCell(c); 
					row.add(cell == null ? null : cell.toString());
				}
			}
		} catch (InvalidFormatException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			fs.close();
		}
		return data;
	}

}
