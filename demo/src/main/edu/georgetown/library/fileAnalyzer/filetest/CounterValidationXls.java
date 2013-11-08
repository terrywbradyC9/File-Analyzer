package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.counter.CounterStat;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.CounterValidation;
import gov.nara.nwts.ftapp.filter.CSVFilter;
import gov.nara.nwts.ftapp.filter.CounterFilterXls;
import gov.nara.nwts.ftapp.filter.ExcelFilter;
import gov.nara.nwts.ftapp.filter.TxtFilter;
import gov.nara.nwts.ftapp.importer.DelimitedFileReader;
import gov.nara.nwts.ftapp.stats.Stats;

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

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
public class CounterValidationXls extends CounterValidation { 
	public CounterValidationXls(FTDriver dt) {
		super(dt);
	}
    @Override public Vector<Vector<String>> getDataFromFile(File f, Stats s) {
		String ext = getExt(f);
		String sep = getSeparator(f, ext);
		try {
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			if (ext.equals("CSV") || ext.endsWith("TXT")) {
				data = DelimitedFileReader.parseFile(f, sep);
			} else {
				data = readExcel(f);
			}
			return data;
		} catch (InvalidFormatException e) {
			s.setVal(CounterStatsItems.Stat, CounterStat.UNSUPPORTED_FILE);
			s.setVal(CounterStatsItems.Message, e.toString());
		} catch (IOException e) {
			s.setVal(CounterStatsItems.Stat, CounterStat.UNSUPPORTED_FILE);
			s.setVal(CounterStatsItems.Message, e.toString());
		} catch (org.apache.poi.hssf.OldExcelFormatException e) {
			s.setVal(CounterStatsItems.Stat, CounterStat.UNSUPPORTED_FILE);
			s.setVal(CounterStatsItems.Message, e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			s.setVal(CounterStatsItems.Stat, CounterStat.UNSUPPORTED_FILE);
			s.setVal(CounterStatsItems.Message, e.toString());
		}
		return null;
    }
    
	Vector<Vector<String>> readExcel(File f) throws InvalidFormatException, IOException {		
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
	
	public String toString() {
		return "Counter Compliance";
	}
	public void initFilters() {
		filters.add(new CounterFilterXls());
		filters.add(new CSVFilter());
		filters.add(new ExcelFilter());
		filters.add(new TxtFilter());
	}
	
	
	
}
