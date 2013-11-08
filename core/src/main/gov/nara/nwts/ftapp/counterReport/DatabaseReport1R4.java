package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class DatabaseReport1R4 extends DatabaseReport1 {
	public static final String NAME = "Database Report 1";
	public DatabaseReport1R4() {
		super(REV.R4, "Total Searches, Result Clicks and Record Views by Month and Database");
	}
	
	public static final String[] FIELDS = {"Regular Searches", "Searches-federated and automated", "Result Clicks", "Record Views"};
	@Override public void initCustom(CounterData data) {
		addCheck("A8", new StaticCounterCheck("Database"));
		addCheck("B8", new StaticCounterCheck("Publisher"));
		addCheck("C8", new StaticCounterCheck("Platform"));
		addCheck("D8", new StaticCounterCheck("User Activity"));
		addCheck("E8", new StaticCounterCheck("Reporting Period Total"));
		
		this.checkColHeader(data);
		checkFields(data, getDataRow(), getTotalCol(data)-1, FIELDS);		
	}
	
	public boolean isSupported() {
		return true;
	}
	public int getHeadRow() {return 7;}
	public int getDataRow() {return 8;}
	
	public int getFirstDataCol() {return 5;}
	public int getLastCol(CounterData data) {return data.getLastCol(getHeadRow());}
	public int getLastDataCol(CounterData data) {return getLastCol(data);}
	public int getLastDataColWithVal(CounterData data) {return data.getLastCol(getDataRow(), data.getLastCol(getDataRow()));}
	public int getTotalCol(CounterData data) {return 4;}

}
