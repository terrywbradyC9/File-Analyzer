package edu.georgetown.library.fileAnalyzer.counter;

class StaticCounterCheck extends CounterCheck {
	String val;
	StaticCounterCheck(String val) {
		this.val = val;
		this.message = "Expected value (case sensitive): " + val;
	}
	@Override
	CheckResult performCheck(Cell cell, String cellval) {
		if (val.equals(cellval)) {
			return CheckResult.createCellValid(cell);
		}
		
		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message);
		if (stat == CounterStat.FIXABLE) {
			res.setNewVal(val);
		}
		return res;
	}
}

