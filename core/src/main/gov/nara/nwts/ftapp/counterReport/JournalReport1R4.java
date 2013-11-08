package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.CounterData;
import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;
import gov.nara.nwts.ftapp.counter.StaticCounterCheck;

public class JournalReport1R4 extends JournalReport1 {
	public JournalReport1R4() {
		super(REV.R4);
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("A8", new StaticCounterCheck("Journal"));
		addCheck("B8", new StaticCounterCheck("Publisher"));
		addCheck("C8", new StaticCounterCheck("Platform"));
		addCheck("D8", new StaticCounterCheck("Journal DOI"));
		addCheck("E8", new StaticCounterCheck("Proprietary Identifier"));
		addCheck("F8", new StaticCounterCheck("Print ISSN"));
		addCheck("G8", new StaticCounterCheck("Online ISSN"));
		addCheck("H8", new StaticCounterCheck("Reporting Period Total"));
		addCheck("I8", new StaticCounterCheck("Reporting Period HTML"));
		addCheck("J8", new StaticCounterCheck("Reporting Period PDF"));
		
		int C_JOURNAL = 0;//a
		int C_PUB = 1;//b
		int C_PLAT = 2;//c
		//int C_DOI = 3;//d
		//int C_PROP = 4;//e
		//int C_PRINT = 5;//f
		//int C_ONLINE = 6;//g
		
		addCheck("A9", new StaticCounterCheck("Total for all journals"));
		//B9, publisher/vendor, may be blank
		addCheck("C9", ReportType.NONBLANK); //platform
		addCheck("D9", ReportType.BLANK);
		addCheck("E9", ReportType.BLANK);
		addCheck("F9", ReportType.BLANK);
		addCheck("G9", ReportType.BLANK);

		addCheckRange(ReportType.NONBLANK, getDataRow(), C_JOURNAL, data.getLastRow(), C_JOURNAL); 
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_PUB, data.getLastRow(), C_PUB); 
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_PLAT, data.getLastRow(), C_PLAT); 
		//addCheckRange(ReportType.NONBLANK, getDataRow(), C_DOI, data.getLastRow(), C_DOI); 
		//addCheckRange(ReportType.NONBLANK, getDataRow(), C_PROP, data.getLastRow(), C_PROP); 
		//addCheckRange(ReportType.NONBLANK, getDataRow(), C_PRINT, data.getLastRow(), C_PRINT); 
		//addCheckRange(ReportType.NONBLANK, getDataRow(), C_ONLINE, data.getLastRow(), C_ONLINE); 

		checkColHeader(data);
		checkGrid(data);
	}
	public int getHeadRow() {return 7;}
	public int getTotalRow() {return 8;}
	public int getDataRow() {return 9;}
	
	public int getFirstDataCol() {return 10;}
	public int getLastCol(CounterData data) {return data.getLastCol(getHeadRow());}
	public int getLastDataCol(CounterData data) {return getLastCol(data);}
	public int getLastDataColWithVal(CounterData data) {return data.getLastCol(getDataRow(), data.getLastCol(getDataRow()));}
	public int getTotalCol(CounterData data) {return 7;}
	public int getHtmlTotalCol(CounterData data) {return 8;}
	public int getPdfTotalCol(CounterData data) {return 9;}
}
