package edu.georgetown.library.fileAnalyzer.counter;

class SumCounterCheck extends IntCounterCheck {
	String val;

	SumCounterCheck(String message) {
		super(message);
	}

	@Override
	CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		CheckResult res = super.performCheck(cd, cell, cellval);
		if (res.stat != CounterStat.VALID) return res;
		
		int cursum = getIntValue(cellval);

		int rangesum = getRangeSum(cd, cell);
		
		if (cursum == rangesum) {
			return CheckResult.createCellValid(cell);
		}
		return CheckResult.createCellInvalid(cell, message + ". Invalid range sum (expected:" + rangesum + "; current:" + cursum+")").setNewVal(""+rangesum);
	}
	
	public int getRangeSum(CounterData cd, Cell cell) {
		return 0;
	}
	
}

