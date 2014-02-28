package edu.georgetown.library.fileAnalyzer.filetest;

import edu.georgetown.library.fileAnalyzer.importer.ExcelReader;
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
import java.io.IOException;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

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
				data = ExcelReader.readExcel(f);
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
