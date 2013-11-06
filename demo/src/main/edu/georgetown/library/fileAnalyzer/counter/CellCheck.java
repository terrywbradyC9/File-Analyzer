package edu.georgetown.library.fileAnalyzer.counter;

import java.util.ArrayList;
import java.util.List;

public class CellCheck {
	ArrayList<Cell> cells = new ArrayList<Cell>();
	
	CounterCheck check;
	CellCheck(CounterCheck check) {
		this.check = check;
	}
	CellCheck(CounterCheck check, Cell cell) {
		this.check = check;
		cells.add(cell);
	}
	CellCheck(CounterCheck check, List<Cell> acell) {
		this.check = check;
		for(Cell cell: acell){
			cells.add(cell);
		}
	}
	List<CheckResult> performCheck(CounterData cd) {
		ArrayList<CheckResult> results = new ArrayList<CheckResult>();
		for(Cell cell: cells) {
			results.add(check.performCheck(cd, cell, cd.getCellValue(cell)));
		}
		return results;
	}

}
