package edu.georgetown.library.fileAnalyzer.counter;

class StaticCounterCheck extends CounterCheck {
	String val;
	StaticCounterCheck(String val) {
		this.val = val;
		this.message = "Expected value: " + val;
	}
	@Override
	CheckResult performCheck(Cell cell, String cellval) {
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
		
		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message+": "+cellval);
		if (val.equalsIgnoreCase(cellval)) {
			res.setMessage("Case Mismatch. " + message);
		} 
		
		if (stat == CounterStat.FIXABLE) {
			res.setNewVal(val);
		}
		return res;
	}
	
}

