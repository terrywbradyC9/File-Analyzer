package gov.nara.nwts.ftapp.counter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ReportType {
	
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
		addCheckStandard(data);
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
	
	public void checkGrid(CounterData data) {
		addCheckRange(new ColSumCounterCheck(getDataRow(), data.getLastRow(), "Cell should be total of Col"), getTotalRow(), getFirstDataCol(), getTotalRow(), getLastDataColWithVal(data)); 
		addCheckRange(new ColSumCounterCheck(getDataRow(), data.getLastRow(), "Cell should be total of Col"), getTotalRow(), getTotalCol(data), getTotalRow(), getTotalCol(data) +  getTotalCols().length - 1); 

		addCheckRange(new IntCounterCheck("Monthly stat must be a number"), getDataRow(), getFirstDataCol(), data.getLastRow(), getLastDataColWithVal(data)); //Month counts
		addCheckRange(ReportType.BLANK, getDataRow(), getLastDataColWithVal(data) + 1, data.getLastRow(), getLastDataCol(data)); //Month counts

		addCheckRange(new RowSumCounterCheck(getFirstDataCol(), getLastDataCol(data), "Cell should be total of data cols"), getDataRow(), getTotalCol(data), data.getLastRow(), getTotalCol(data)); 
		if (getTotalCols().length > 1) {
			addCheckRange(new IntCounterCheck("Total must be a number"), getDataRow(), getTotalCol(data) + 1, data.getLastRow(), getTotalCol(data) + getTotalCols().length - 1); 			
		}
	}

	public void addCheckStandard(CounterData data) {
		if (rev == REV.R3 || rev == REV.R1) {
			addCheck("A2", ReportType.NONBLANK);
			addCheck("A3", new StaticCounterCheck("Date run:"));
			addCheck("A4", ReportType.YYYYMMDD);
		} else if (rev == REV.R4) {
			addCheck("A2", ReportType.NONBLANK);
			//Institutional Identifier, A3 may be blank
			addCheck("A4", new StaticCounterCheck("Period Covered by Report"));
			addCheck("A5", ReportType.YYYYMMDD_to_YYYYMMDD);
			addCheck("A6", new StaticCounterCheck("Date run"));
			addCheck("A7", ReportType.YYYYMMDD);			
		}
		
		for(int col=0; col<getCols().length; col++) {
			addCheck(getHeadRow(), col, new StaticCounterCheck(getCols()[col]));						
		}
		for(int col=0; col<getTotalCols().length; col++) {
			addCheck(getHeadRow(), getTotalCol(data) + col, new StaticCounterCheck(getTotalCols()[col]).setCounterStat(CounterStat.ERROR));						
		}
	}

	abstract public boolean hasTotalRow();
	public int getHeadRow() {
		if (rev == REV.R3 || rev == REV.R1) return 4;
		if (rev == REV.R4) return 7;
		return 4;
	}
	public int getTotalRow() {
		return hasTotalRow() ? getHeadRow() + 1 : 0;
	}
	
	public int getDataRow() {
		return hasTotalRow() ? getHeadRow() + 2 : getHeadRow() + 1;
	}

	public boolean hasTotalColFirst() {
		 return (rev == REV.R4);
	}
	public int getFirstDataCol() {
		return getCols().length + (hasTotalColFirst() ? getTotalCols().length : 0);
	}
	abstract public String[] getCols();
	public static String[] TCOLS = {"YTD Total"};
	public static String[] TCOLSR4 = {"Reporting Period Total"};
	public String[] getTotalCols() {return (rev == REV.R4) ? TCOLSR4 : TCOLS;}

	public int getTotalCol(CounterData data) {
		return hasTotalColFirst() ? getCols().length : getLastCol(data) - getTotalCols().length + 1;
	}
	public int getLastCol(CounterData data) {
		return data.getLastCol(getHeadRow());
	}
	//override if summary cols at the end
	public int getLastDataCol(CounterData data) {
		return Math.max(0, data.getLastCol(getHeadRow()) - (hasTotalColFirst() ? 0 : getTotalCols().length));
	}
	public int getLastDataColWithVal(CounterData data) {
		return data.getLastCol(getDataRow(), getLastDataCol(data));
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
