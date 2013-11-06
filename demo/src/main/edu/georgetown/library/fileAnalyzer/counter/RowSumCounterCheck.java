package edu.georgetown.library.fileAnalyzer.counter;

class RowSumCounterCheck extends SumCounterCheck {
	String val;
	int sc;
	int ec;
	RowSumCounterCheck(int sc, int ec, String message) {
		super(message);
		this.sc = sc;
		this.ec = ec;
	}

	public int getRangeSum(CounterData cd, Cell cell) {
		int rangesum = 0;
		for(int c=sc; c<=ec; c++) {
			rangesum += getIntValue(cd.getCellValue(new Cell(cell.row,c)), 0);
		}		
		return rangesum;
	}
		
}

