package edu.georgetown.library.fileAnalyzer.counter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DateCounterCheck extends CounterCheck {
	String val;
	String fmt;
	DateCounterCheck(String fmt, String message) {
		this.fmt = fmt;
		this.message = message;
	}

	@Override
	CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval == null) {
			return CheckResult.createCellInvalid(cell, message + ". Null cell value");
		}
		if (cellval.isEmpty()) {
			return CheckResult.createCellInvalid(cell, message + "Empty cell value");
		}
		SimpleDateFormat df = new SimpleDateFormat(fmt);
		Date date;
		try {
			date = df.parse(cellval);
		} catch (ParseException e) {
			return CheckResult.createCellInvalid(cell, message + "Date parse error: " + e.getMessage());
		}
		
		if (date == null) {
			return CheckResult.createCellInvalid(cell, "Date parse error");
		}
		
		String s = df.format(date);
		if (s.equals(cellval)) {
			return CheckResult.createCellValid(cell);			
		}
		
		return CheckResult.createCellFixable(cell, "Date in inproper format", s);
	}
	
}

