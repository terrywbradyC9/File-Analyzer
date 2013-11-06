package edu.georgetown.library.fileAnalyzer.counter;

class IntCounterCheck extends CounterCheck {
	String val;
	IntCounterCheck(String message) {
		this.message = message;
	}

	Integer getIntValue(String cellval) {
		cellval = (cellval == null) ? "" : cellval.replaceAll("\\.\\d+$", "");
		try {
			return Integer.parseInt(cellval);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	Integer getIntValue(String cellval, int def) {
		cellval = (cellval == null) ? "" : cellval.replaceAll("\\.\\d+$", "");
		try {
			return Integer.parseInt(cellval);
		} catch(NumberFormatException e) {
			return def;
		}
	}
	
	@Override
	CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (getIntValue(cellval) == null) {
			return CheckResult.createCellInvalid(cell, "Cells containing sums should be numeric");
		}
		
		return CheckResult.createCellValid(cell);
	}
	
}

