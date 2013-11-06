package edu.georgetown.library.fileAnalyzer.counter;

public class DatabaseReport3 extends ReportType {
	public static final String NAME = "Database Report 3";
	public DatabaseReport3(REV rev, String title) {
		super(NAME, rev, title);
	}
	public DatabaseReport3() {
		super(NAME, REV.R3, "Total Searches and Sessions by Month and Service.");
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("B5", new StaticCounterCheck("Platform"));
		addCheck("C5", ReportType.BLANK);
		
		checkFields(data, getDataRow(), getFirstDataCol()-1, DatabaseReport1.FIELDS);
		this.checkColHeader(data);
		addCheck(getHeadRow(), getLastCol(data), new StaticCounterCheck("YTD Total"));	
	}
	
	boolean isSupported() {
		return true;
	}
	public int getHeadRow() {return 4;}
	public int getDataRow() {return 5;}
	
	public int getFirstDataCol() {return 3;}
	public int getLastCol(CounterData data) {return data.getLastCol(getHeadRow());}
	public int getLastDataCol(CounterData data) {return getLastCol(data) - 1;}
	public int getLastDataColWithVal(CounterData data) {return data.getLastCol(getDataRow(), data.getLastCol(getDataRow()) - 1);}
	public int getTotalCol(CounterData data) {return getLastCol(data);}

}
