package gov.nara.nwts.ftapp.counter;

public class StaticCounterCheck extends CounterCheck {
	String val;
	public StaticCounterCheck(String val) {
		this.val = val;
		this.message = "Expected value: " + val;
	}
	@Override
	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval == null) {
			if (allowNull) {
				return CheckResult.createCellValid(cell);			
			} else {
				return CheckResult.createCellInvalid(cell, message + ": Cell is empty");
			}
		}

		if (val.equals(cellval)) {
			return CheckResult.createCellValid(cell);
		} 
		
		if (val.equalsIgnoreCase(cellval)) {
			return CheckResult.createCellInvalidCase(cell, message).setNewVal(val);
		} 
		if (val.equals(cellval.trim())) {
			return CheckResult.createCellInvalidTrim(cell, message).setNewVal(val);
		} 
		if (val.equalsIgnoreCase(cellval.trim())) {
			return CheckResult.createCellInvalidCase(cell, message).setNewVal(val);
		} 
		if (val.equalsIgnoreCase(cellval.replaceAll("[:\\s]+$", ""))) {
			return CheckResult.createCellInvalidPunct(cell, message).setNewVal(val);
		} 
		
		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message).setNewVal(val);
		return res;
	}
	
}

