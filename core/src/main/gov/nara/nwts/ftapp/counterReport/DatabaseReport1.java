package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.CounterStat;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class DatabaseReport1 extends ReportType {
	public static final String NAME = "Database Report 1";
	public DatabaseReport1(REV rev, String title) {
		super(NAME, rev, title);
	}
	public DatabaseReport1() {
		super(NAME, REV.R3, "Total Searches and Sessions by Month and Database");
	}
	
	public static String[] FIELDS = {"Total searches run","Searches-federated and automated","Total sessions","Sessions-federated and automated"};
	
	@Override public void initCustom(CounterData data) {
		addCheck(getHeadRow(), getLastCol(data), new StaticCounterCheck("YTD Total").setCounterStat(CounterStat.ERROR));	
		addCheck("B5", new StaticCounterCheck("Publisher"));
		addCheck("C5", new StaticCounterCheck("Platform"));
		addCheck("D5", ReportType.BLANK);
		
		checkFields(data, getDataRow(), getFirstDataCol()-1, FIELDS);
		this.checkColHeader(data);
	}
	
	public boolean isSupported() {
		return true;
	}
	public int getHeadRow() {return 4;}
	public int getDataRow() {return 5;}
	
	public int getFirstDataCol() {return 4;}
	public int getLastCol(CounterData data) {return data.getLastCol(getHeadRow());}
	public int getLastDataCol(CounterData data) {return getLastCol(data) - 1;}
	public int getLastDataColWithVal(CounterData data) {return data.getLastCol(getDataRow(), data.getLastCol(getDataRow()) - 1);}
	public int getTotalCol(CounterData data) {return getLastCol(data);}

}
