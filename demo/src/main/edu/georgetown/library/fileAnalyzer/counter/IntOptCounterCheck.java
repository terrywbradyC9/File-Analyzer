package edu.georgetown.library.fileAnalyzer.counter;

public class IntOptCounterCheck extends IntCounterCheck {

	IntOptCounterCheck(String message) {
		super(message);
	}

	CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval.isEmpty()) return CheckResult.createCellValid(cell);
		return super.performCheck(cd, cell, cellval);
	}

}
