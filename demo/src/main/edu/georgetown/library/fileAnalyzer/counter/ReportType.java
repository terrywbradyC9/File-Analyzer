package edu.georgetown.library.fileAnalyzer.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
	}
	
	public final void init(CounterData data) {
		initBase(data);
		initCustom(data);
	}
	public void initCustom(CounterData data) {
		
	}
	public final void initBase(CounterData data) {
		checks.clear();
		if (title != null) {
			addCheck("B1",new StaticCounterCheck(title).setCounterStat(CounterStat.FIXABLE));
		}
		//addCheck(BLANK, 0, 1, 0, data.getMaxCol(0));
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

	public static CounterCheck BLANK = new StaticCounterCheck("").setCounterStat(CounterStat.WARNING).setMessage("Extra cells should be blank").setAllowNull(true);
	public static CounterCheck NONBLANK = new PatternCounterCheck(Pattern.compile(".+")).setCounterStat(CounterStat.INVALID).setMessage("Cell cannot be blank");
	
	public static Pattern pYYYYMMDD = Pattern.compile("^\\d\\d\\d\\d-(01|02|03|04|05|06|07|08|09|10|11|12)-(0[1-9]|[12][0-9]|30|31|32)$");
	public static Pattern pYYYYMMDDx = Pattern.compile("^(\\d\\d\\d\\d-(01|02|03|04|05|06|07|08|09|10|11|12)-(0[1-9]|[12][0-9]|30|31|32)).*$");
	public static CounterCheck YYYYMMDD = new PatternCounterCheck(pYYYYMMDD, pYYYYMMDDx, "$1").setCounterStat(CounterStat.INVALID).setMessage("Cell must be in YYYY-MM-DD format");

}
