package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;

public class DatabaseReport1R4 extends DatabaseReport1 {
	public static final String NAME = "Database Report 1";
	public DatabaseReport1R4() {
		super(REV.R4, "Total Searches, Result Clicks and Record Views by Month and Database");
	}
	
	public static final String[] FIELDS = {"Regular Searches", "Searches-federated and automated", "Result Clicks", "Record Views"};
	public static String[] COLS = {"Database","Publisher","Platform","User Activity"};
	public String[] getCols() {return COLS;}

	@Override public void initCustom(CounterData data) {
		checkColHeader(data);
		checkFields(data, getDataRow(), getTotalCol(data)-1, FIELDS);		
	}
	
	public boolean isSupported() {
		return true;
	}

}
