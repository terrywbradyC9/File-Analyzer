package edu.georgetown.library.fileAnalyzer.counter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

class DateCounterCheck extends CounterCheck {
	String val;
	String fmtDisp;
	String fmtParse;
	SimpleDateFormat dfParse;
	SimpleDateFormat dfDisp;
	static SimpleDateFormat defdf = new SimpleDateFormat("MM/dd/yyyy");
	static SimpleDateFormat def2df = new SimpleDateFormat("yy-MMM");
	static Date y2000 =  new GregorianCalendar(2000,1,1).getTime();
	static {
		defdf.set2DigitYearStart(y2000);
		def2df.set2DigitYearStart(y2000);
	}
	
	DateCounterCheck(String fmtDisp, String fmtParse, String message) {
		this.fmtDisp = fmtDisp;
		this.fmtParse = fmtParse;
		this.message = message;
		this.dfParse = new SimpleDateFormat(fmtParse);
		this.dfParse.set2DigitYearStart(y2000);
		this.dfDisp = new SimpleDateFormat(fmtDisp);
	}
	
	DateCounterCheck(String fmtDisp, String message) {
		this(fmtDisp, fmtDisp, message);
	}

	Date getDate(String cellval, SimpleDateFormat cdf) {
		Date date = null;
		try {
			date = cdf.parse(cellval);
		} catch (ParseException e) {
		}
		return date;
	}

	Date getDate(String cellval) {
		Date date = getDate(cellval, dfParse);
		if (date == null) {
			date = getDate(cellval, defdf);
		}
		if (date == null) {
			date = getDate(cellval, def2df);
		}
		return date;
	}

	@Override
	CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval == null) {
			return CheckResult.createCellInvalid(cell, message + ". Null cell value");
		}
		if (cellval.isEmpty()) {
			return CheckResult.createCellInvalid(cell, message + "Empty cell value");
		}
		Date date = getDate(cellval);
		
		if (date == null) {
			return CheckResult.createCellInvalid(cell, "Date parse error");
		}
		
		String s = dfDisp.format(date);
		if (s.equals(cellval)) {
			return CheckResult.createCellValid(cell);			
		}
		
		return CheckResult.createCellInvalidDate(cell, "Date in inproper format").setNewVal(s);
	}
	
}

