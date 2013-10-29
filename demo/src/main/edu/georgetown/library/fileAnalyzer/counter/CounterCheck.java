package edu.georgetown.library.fileAnalyzer.counter;

public class CounterCheck {
	String message = "Cell does not match specifications";
	CounterStat stat = CounterStat.INVALID;
	CheckResult performCheck(Cell cell, String cellval) {
		return CheckResult.createCellValid(cell);
	}
	
	CounterCheck setMessage(String message) {
		this.message = message;
		return this;
	}
	
	CounterCheck setCounterStat(CounterStat stat) {
		this.stat = stat;
		return this;
	}
	

}
