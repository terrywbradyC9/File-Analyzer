package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class JournalReport1R4 extends JournalReport1 {
	public JournalReport1R4() {
		super(REV.R4);
	}
	
	public static String[] COLS = {"Journal","Publisher","Platform","Journal DOI","Proprietary Identifier","Print ISSN","Online ISSN"};
	public String[] getCols() {return COLS;}
	public static String[] TCOLS = {"Reporting Period Total","Reporting Period HTML","Reporting Period PDF"};
	public String[] getTotalCols() {return TCOLS;}

	@Override public void initCustom(CounterData data) {
		addCheck("A9", new StaticCounterCheck("Total for all journals"));
		//B9, publisher/vendor, may be blank
		addCheck("C9", ReportType.NONBLANK); //platform
		addCheck("D9", ReportType.BLANK);
		addCheck("E9", ReportType.BLANK);
		addCheck("F9", ReportType.BLANK);
		addCheck("G9", ReportType.BLANK);

		addCheckRange(ReportType.NB_JOURNAL, getDataRow(), 0, data.getLastRow(), 0); //journal
		addCheckRange(ReportType.NB_PLATFORM, getDataRow(), 2, data.getLastRow(), 2); //plat
		addCheckRange(ReportType.ISSN, getDataRow(), 5, data.getLastRow(), 6); 

		checkColHeader(data);
		checkGrid(data);
	}
}
