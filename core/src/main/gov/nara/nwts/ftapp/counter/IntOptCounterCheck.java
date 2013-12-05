package gov.nara.nwts.ftapp.counter;

public class IntOptCounterCheck extends IntCounterCheck {

	public IntOptCounterCheck(String message) {
		super(message);
	}

	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval.isEmpty()) return CheckResult.createCellValid(cell);
		return super.performCheck(cd, cell, cellval);
	}

}
