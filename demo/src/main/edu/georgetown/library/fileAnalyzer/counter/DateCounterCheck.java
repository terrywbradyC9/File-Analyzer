package edu.georgetown.library.fileAnalyzer.counter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateCounterCheck extends CounterCheck {
	String val;
	String fmt;
	SimpleDateFormat df;
	static SimpleDateFormat defdf = new SimpleDateFormat("MM/dd/yyyy");
	
	DateCounterCheck(String fmt, String message) {
		this.fmt = fmt;
		this.message = message;
		this.df = new SimpleDateFormat(fmt);
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
		Date date = getDate(cellval, df);
		if (date == null) {
			date = getDate(cellval, defdf);
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
		
		String s = df.format(date);
		if (s.equals(cellval)) {
			return CheckResult.createCellValid(cell);			
		}
		
		return CheckResult.createCellInvalidDate(cell, "Date in inproper format").setNewVal(s);
	}
	
}

