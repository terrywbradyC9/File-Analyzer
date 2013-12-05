package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class BookReport1R4 extends ReportType {
	public static final String NAME = "Book Report 1";
	public BookReport1R4() {
		super(NAME, REV.R4, "Number of Successful Title Requests by Month and Title");
	}
	
	public static String[] COLS = {"","Publisher","Platform","Book DOI","Proprietary Identifier","ISBN","ISSN"};

	public String[] getCols() {return COLS;}
	
	@Override public void initCustom(CounterData data) {
		addCheck("A9", new StaticCounterCheck("Total for all titles"));
		checkColHeader(data);
		checkGrid(data);
	}
	
	public boolean isSupported() {
		return true;
	}
	public boolean hasTotalRow() {
		return true;
	}

}
