package gov.nara.nwts.ftapp.counter;

public class IntCounterCheck extends CounterCheck {
	String val;
	public IntCounterCheck(String message) {
		this.message = message;
	}

	public Integer getIntValue(String cellval) {
		cellval = (cellval == null) ? "" : cellval.replaceAll("\\.\\d+$", "");
		try {
			return Integer.parseInt(cellval);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	public Integer getIntValue(String cellval, int def) {
		cellval = (cellval == null) ? "" : cellval.replaceAll("\\.\\d+$", "");
		try {
			return Integer.parseInt(cellval);
		} catch(NumberFormatException e) {
			return def;
		}
	}
	
	@Override
	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (getIntValue(cellval) == null) {
			return CheckResult.createCellInvalid(cell, "Cells should be numeric");
		}
		
		return CheckResult.createCellValid(cell);
	}
	
}

