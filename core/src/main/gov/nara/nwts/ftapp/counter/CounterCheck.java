package gov.nara.nwts.ftapp.counter;

public class CounterCheck {
	String message = "Cell does not match specifications";
	CounterStat stat = CounterStat.INVALID;
	boolean allowNull = false;
	boolean ignoreVal = false;
	
	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		return CheckResult.createCellValid(cell);
	}
	
	public CounterCheck setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public CounterCheck setCounterStat(CounterStat stat) {
		this.stat = stat;
		return this;
	}
	
	public CounterCheck setAllowNull(boolean b) {
		allowNull = b;
		return this;
	}

	public CounterCheck setIgnoreVal(boolean b) {
		this.ignoreVal = b;
		return this;
	}
}
