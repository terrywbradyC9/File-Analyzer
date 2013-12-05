package gov.nara.nwts.ftapp.counter;

public class CheckResult {
	CounterRec rec;
	public Cell cell;
	public CounterStat stat;
	public String message = "";
	public String newVal;
	
	CheckResult(Cell cell, CounterStat stat) {
		this.rec = CounterRec.CELL;
		this.cell = cell;
		this.stat = stat;
	}

	CheckResult(CounterStat stat) {
		this.rec = CounterRec.FILE;
		this.stat = stat;
	}
	
	CheckResult setMessage(String message) {
		this.message = message;
		return this;
	}
	
	CheckResult setNewVal(String newVal) {
		this.newVal = newVal;
		return this;
	}
	
	static CheckResult createFileStatus(CounterStat stat) {
		return new CheckResult(stat);
	}
	static CheckResult createCellStatus(Cell cell, CounterStat stat) {
		return new CheckResult(cell, stat);
	}
	static CheckResult createCellValid(Cell cell) {
		return createCellStatus(cell, CounterStat.VALID);
	}
	static CheckResult createCellWarning(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.WARNING).setMessage(message);
	}
	static CheckResult createCellInvalid(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.INVALID).setMessage(message);
	}
	static CheckResult createCellInvalidCase(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.WARNING_CASE).setMessage(message);
	}
	static CheckResult createCellInvalidPunct(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.WARNING_PUNCT).setMessage(message);
	}
	static CheckResult createCellInvalidTrim(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.WARNING_TRIM).setMessage(message);
	}
	static CheckResult createCellInvalidDate(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.WARNING_DATE).setMessage(message);
	}
	static CheckResult createCellInvalidSum(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.INVALID_SUM).setMessage(message);
	}
	static CheckResult createCellError(Cell cell, String message) {
		return createCellStatus(cell, CounterStat.ERROR).setMessage(message);
	}

}