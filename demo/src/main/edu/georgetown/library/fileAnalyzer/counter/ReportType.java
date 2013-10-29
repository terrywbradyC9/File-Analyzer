package edu.georgetown.library.fileAnalyzer.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportType {
	static HashMap<String,ReportType> reportTypes = new HashMap<String,ReportType>();
	ArrayList<CellCheck> checks = new ArrayList<CellCheck>();
	int defRuleCount = 0;
	
	String name;
	String title;

	public ReportType(String name) {
		this(name, null);
	}
	public ReportType(String name, String title) {
		this.name = name;
		this.title = title;
		reportTypes.put(name, this);
		
		if (title != null) {
			addCheck("B1",new StaticCounterCheck(title).setCounterStat(CounterStat.FIXABLE));
		}
		CounterCheck blank = new StaticCounterCheck("").setCounterStat(CounterStat.WARNING).setMessage("Extra cells should be blank");
		addCheck("A2",	blank);
		defRuleCount = checks.size();
	}
	
	void addCheck(String cell, CounterCheck ccheck) {
		checks.add(new CellCheck(ccheck, new Cell(cell)));
	}

	void addCheck(CounterCheck ccheck, int srow, int scol, int endrow, int endcol) {
		checks.add(new CellCheck(ccheck, Cell.makeRange(srow, scol, endrow, endcol)));
	}
	
	boolean isSupported() {
		return checks.size() > defRuleCount;
	}
	List<CheckResult> validate(CounterData cd) {
		ArrayList<CheckResult> results = new ArrayList<CheckResult>();
		for(CellCheck check: checks) {
			results.addAll(check.performCheck(cd));
		}
		return results;
	}

}
