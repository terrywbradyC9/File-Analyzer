package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class JournalReport1 extends ReportType {
	public static final String NAME = "Journal Report 1";
	public JournalReport1(REV rev) {
		super(NAME, rev, "Number of Successful Full-text Article Requests by Month and Journal");
	}
	public JournalReport1() {
		this(REV.R3);
	}
	
	public static String[] COLS = {"","Publisher","Platform","Print ISSN","Online ISSN"};
	public String[] getCols() {return COLS;}
	public static String[] TCOLS = {"YTD Total","YTD HTML","YTD PDF"};
	public String[] getTotalCols() {return TCOLS;}

	@Override public void initCustom(CounterData data) {
		addCheck("A6", new StaticCounterCheck("Total for all journals"));
		addCheck("C6", ReportType.NONBLANK);
		addCheck("D6", ReportType.BLANK);
		addCheck("E6", ReportType.BLANK);

		addCheckRange(ReportType.NONBLANK, getDataRow(), 0, data.getLastRow(), 0); //Journal
		addCheckRange(ReportType.NONBLANK, getDataRow(), 1, data.getLastRow(), 1); //Pub
		addCheckRange(ReportType.NONBLANK, getDataRow(), 2, data.getLastRow(), 2); //Plat
		addCheckRange(ReportType.NONBLANK, getDataRow(), 3, data.getLastRow(), 3); //Print
		addCheckRange(ReportType.NONBLANK, getDataRow(), 4, data.getLastRow(), 4); //Online

		checkColHeader(data);
		checkGrid(data);
	}
	
	public boolean isSupported() {
		return true;
	}
	
	public boolean hasTotalRow() {return true;}

}
