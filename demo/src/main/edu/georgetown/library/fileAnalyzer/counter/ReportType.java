package edu.georgetown.library.fileAnalyzer.counter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class ReportType {
	
	static HashMap<RPT,ReportType> reportTypes = new HashMap<RPT,ReportType>();
	ArrayList<CellCheck> checks = new ArrayList<CellCheck>();
	int defRuleCount = 0;
	
	RPT rpt;
	String title;
	
	public ReportType(String name, REV rev) {
		this(name, rev, null);
	}
	public ReportType(String name, REV rev, String title) {
		this.rpt = new RPT(name, rev);
		this.title = title;
		reportTypes.put(rpt, this);
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
			addCheck("B1",new StaticCounterCheck(title).setCounterStat(CounterStat.WARNING));
		}
		defRuleCount = checks.size();		
	}
	
	void addCheck(String cell, CounterCheck ccheck) {
		checks.add(new CellCheck(ccheck, new Cell(cell)));
	}

	void addCheck(int row, int col, CounterCheck ccheck) {
		checks.add(new CellCheck(ccheck, new Cell(row, col)));
	}

	void addCheckRange(CounterCheck ccheck, int srow, int scol, int endrow, int endcol) {
		checks.add(new CellCheck(ccheck, Cell.makeRange(srow, scol, endrow, endcol)));
	}
	
	boolean isSupported() {
		return false;
	}
	List<CheckResult> validate(CounterData cd) {
		ArrayList<CheckResult> results = new ArrayList<CheckResult>();
		for(CellCheck check: checks) {
			results.addAll(check.performCheck(cd));
		}
		return results;
	}

	public static CounterCheck BLANK = new StaticCounterCheck("").setCounterStat(CounterStat.WARNING).setMessage("Extra cells should be blank").setAllowNull(true);
	public static CounterCheck NONBLANK = new PatternCounterCheck(Pattern.compile(".+")).setCounterStat(CounterStat.INVALID_BLANK).setMessage("Non-blank value expected");
	
	public static String sYYYYMMDD = "\\d\\d\\d\\d-(01|02|03|04|05|06|07|08|09|10|11|12)-(0[1-9]|[12][0-9]|30|31|32)";
	public static Pattern pYYYYMMDD_to_YYYYMMDD = Pattern.compile("^" + sYYYYMMDD + " to " + sYYYYMMDD + "$");
	public static CounterCheck YYYYMMDD_to_YYYYMMDD = new PatternCounterCheck(pYYYYMMDD_to_YYYYMMDD).setCounterStat(CounterStat.INVALID).setMessage("Cell must be in 'YYYY-MM-DD to YYYY-MM-DD' format");

	public static CounterCheck MMMYYYY = new DateCounterCheck("MMM-yyyy", "Month header must be in Mmm-YYYY format").setCounterStat(CounterStat.INVALID);
	public static CounterCheck YYYYMMDD = new DateCounterCheck("yyyy-MM-dd", "Date must be in YYYY-MM-DD format").setCounterStat(CounterStat.INVALID);

	public static Pattern pINT = Pattern.compile("^(\\d+)$");
	public static CounterCheck INT = new PatternCounterCheck(pINT).setCounterStat(CounterStat.INVALID).setMessage("Count cell must contain a number");
}
