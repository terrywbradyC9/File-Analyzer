package gov.nara.nwts.ftapp.counter;

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
			CheckResult res = check.performCheck(cd, cell, cd.getCellValue(cell)); 
			results.add(res);
			
			if (res.stat != CounterStat.VALID) {
				if (res.newVal != null) {
					CounterData.setCellValue(cd.getFix(false), res.cell, res.newVal);
				} else {
					if (check.ignoreVal) {
						res.setIgnoreVal(true);
					}
				}
			}
			
			if (res.stat.ordinal() >= CounterStat.ERROR.ordinal()) {
				break;
			}
		}
		return results;
	}

}
