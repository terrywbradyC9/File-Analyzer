package gov.nara.nwts.ftapp.counter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReportType {
	
	ArrayList<CellCheck> checks = new ArrayList<CellCheck>();
	
	String name;
	REV rev;
	String title;
	
	public ReportType(String name, REV rev) {
		this(name, rev, null);
	}
	public ReportType(String name, REV rev, String title) {
		this.name = name;
		this.rev = rev;
		this.title = title;
	}
	
	public final void init(CounterData data) {
		initBase(data);
		initCustom(data);
	}
	public void initCustom(CounterData data) {
		
	}
	public final void initBase(CounterData data) {
		checks.clear();
		addCheck("A1",new StaticCounterCheck(name + " (" + rev.name() + ")"));
		if (title != null) {
			addCheck("B1",new StaticCounterCheck(title).setCounterStat(CounterStat.WARNING));
		}
		addCheckStandard();
	}
	
	protected void addCheck(String cell, CounterCheck ccheck) {
		checks.add(new CellCheck(ccheck, new Cell(cell)));
	}

	protected void addCheck(int row, int col, CounterCheck ccheck) {
		checks.add(new CellCheck(ccheck, new Cell(row, col)));
	}

	protected void addCheckRange(CounterCheck ccheck, int srow, int scol, int endrow, int endcol) {
		checks.add(new CellCheck(ccheck, Cell.makeRange(srow, scol, endrow, endcol)));
	}
	
	public boolean isSupported() {
		return false;
	}
	List<CheckResult> validate(CounterData cd) {
		ArrayList<CheckResult> results = new ArrayList<CheckResult>();
		for(CellCheck check: checks) {
			results.addAll(check.performCheck(cd));
			if (results.size() > 0) {
				CheckResult last = results.get(results.size()-1); 
				if (last.stat.ordinal() >= CounterStat.ERROR.ordinal()){
					break;
				}
			}
		}
		return results;
	}

	public void checkColHeader(CounterData data) {
		for(int c=getFirstDataCol(); c <= getLastDataCol(data); c++) {
			addCheck(getHeadRow(), c, ReportType.MMMYYYY);			
		}		
	}
	
	public void checkFields(CounterData data, int dr, int fieldCol, String[] fields) {
		for(int datarow = dr; data.getLastCol(datarow) > 0; datarow += fields.length){
			addCheckRange(ReportType.NONBLANK, datarow, 0, datarow+fields.length-1, 2);
			for(int i=0; i<fields.length; i++) {
				addCheck(datarow + i, fieldCol, new StaticCounterCheck(fields[i]));				
			}
			addCheckRange(new IntCounterCheck("Monthly stat must be a number"), datarow, getFirstDataCol(), datarow + fields.length - 1, getLastDataColWithVal(data)); //Month counts
			addCheckRange(new RowSumCounterCheck(getFirstDataCol(), getLastDataCol(data), "Cell should be total of data cp;"), datarow, getTotalCol(data), datarow + fields.length - 1, getTotalCol(data)); 
		}		
	}
	
	public void addCheckStandard() {
		if (rev == REV.R3) {
			addCheck("A2", ReportType.NONBLANK);
			addCheck("A3", new StaticCounterCheck("Date run:"));
			addCheck("A4", ReportType.YYYYMMDD);
			addCheck("A5", ReportType.BLANK);
		} else if (rev == REV.R4) {
			addCheck("A2", ReportType.NONBLANK);
			//Institutional Identifier, A3 may be blank
			addCheck("A4", new StaticCounterCheck("Period Covered by Report"));
			addCheck("A5", ReportType.YYYYMMDD_to_YYYYMMDD);
			addCheck("A6", new StaticCounterCheck("Date run"));
			addCheck("A7", ReportType.YYYYMMDD);			
		}
	}

	public int getHeadRow() {
		return 0;
	}
	public int getFirstDataCol() {
		return 0;
	}
	public int getLastDataCol(CounterData data) {
		return 0;
	}
	public int getLastDataColWithVal(CounterData data) {
		return 0;
	}
	public int getTotalCol(CounterData data) {
		return 0;
	}

	public static CounterCheck BLANK = new StaticCounterCheck("").setCounterStat(CounterStat.WARNING).setMessage("Blank cell expected").setAllowNull(true);
	public static CounterCheck NONBLANK = new PatternCounterCheck(Pattern.compile(".+")).setCounterStat(CounterStat.INVALID_BLANK).setMessage("Non-blank value expected");
	
	public static String sYYYYMMDD = "\\d\\d\\d\\d-(01|02|03|04|05|06|07|08|09|10|11|12)-(0[1-9]|[12][0-9]|30|31|32)";
	public static Pattern pYYYYMMDD_to_YYYYMMDD = Pattern.compile("^" + sYYYYMMDD + " to " + sYYYYMMDD + "$");
	public static CounterCheck YYYYMMDD_to_YYYYMMDD = new PatternCounterCheck(pYYYYMMDD_to_YYYYMMDD).setCounterStat(CounterStat.INVALID).setMessage("Cell must be in 'YYYY-MM-DD to YYYY-MM-DD' format");

	public static CounterCheck MMMYYYY = new DateCounterCheck("MMM-yyyy", "MMM-yy", "Month header must be in Mmm-YYYY format").setCounterStat(CounterStat.INVALID);
	public static CounterCheck YYYYMMDD = new DateCounterCheck("yyyy-MM-dd", "Date must be in YYYY-MM-DD format").setCounterStat(CounterStat.INVALID);

	public static Pattern pINT = Pattern.compile("^(\\d+)$");
	public static CounterCheck INT = new PatternCounterCheck(pINT).setCounterStat(CounterStat.INVALID).setMessage("Count cell must contain a number");
}
