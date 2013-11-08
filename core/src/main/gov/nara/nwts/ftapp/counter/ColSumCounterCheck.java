package gov.nara.nwts.ftapp.counter;

public class ColSumCounterCheck extends SumCounterCheck {
	String val;
	int sr;
	int er;
	public ColSumCounterCheck(int sr, int er, String message) {
		super(message);
		this.sr = sr;
		this.er = er;
	}

	public int getRangeSum(CounterData cd, Cell cell) {
		int rangesum = 0;
		for(int r=sr; r<=er; r++) {
			rangesum += getIntValue(cd.getCellValue(new Cell(r, cell.col)), 0);
		}		
		return rangesum;
	}

}

