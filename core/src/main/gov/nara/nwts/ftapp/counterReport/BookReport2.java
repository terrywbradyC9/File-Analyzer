package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;

public class BookReport2 extends ReportType {
	public static final String NAME = "Book Report 2";
	public BookReport2() {
		super(NAME, REV.R1, "Number of Successful Section Requests by Month and Title");
	}
	
	public static String[] COLS = {"","Publisher","Platform","ISBN","ISSN"};
	public String[] getCols() {return COLS;}
	
	@Override public void initCustom(CounterData data) {
		checkColHeader(data);
		checkGrid(data);

		addCheckRange(ReportType.NB_PLATFORM, getDataRow(), 2, data.getLastRow(), 2); //Plat
		addCheckRange(ReportType.ISBN, getDataRow(), 3, data.getLastRow(), 3); 
		addCheckRange(ReportType.ISSN, getDataRow(), 4, data.getLastRow(), 4); 
	}
	
	public boolean isSupported() {
		return true;
	}
	public boolean hasTotalRow() {
		return true;
	}

}
