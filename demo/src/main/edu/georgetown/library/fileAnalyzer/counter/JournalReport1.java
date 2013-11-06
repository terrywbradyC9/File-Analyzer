package edu.georgetown.library.fileAnalyzer.counter;

public class JournalReport1 extends ReportType {
	public static final String NAME = "Journal Report 1";
	public JournalReport1(REV rev) {
		super(NAME, rev, "Number of Successful Full-text Article Requests by Month and Journal");
	}
	public JournalReport1() {
		this(REV.R3);
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("B5", new StaticCounterCheck("Publisher"));
		addCheck("C5", new StaticCounterCheck("Platform"));
		addCheck("D5", new StaticCounterCheck("Print ISSN"));
		addCheck("E5", new StaticCounterCheck("Online ISSN"));
		
		int C_JOURNAL = 0;
		int C_PUB = 1;
		int C_PLAT = 2;
		int C_PRINT = 3;
		int C_ONLINE = 4;
		
		addCheck(getHeadRow(), getTotalCol(data), new StaticCounterCheck("YTD Total"));
		addCheck(getHeadRow(), getHtmlTotalCol(data), new StaticCounterCheck("YTD HTML"));
		addCheck(getHeadRow(), getPdfTotalCol(data), new StaticCounterCheck("YTD PDF"));
		
		addCheck("A6", new StaticCounterCheck("Total for all journals"));
		addCheck("C6", ReportType.NONBLANK);
		addCheck("D6", ReportType.BLANK);
		addCheck("E6", ReportType.BLANK);

		addCheckRange(ReportType.NONBLANK, getDataRow(), C_JOURNAL, data.getLastRow(), C_JOURNAL); //A7-An
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_PUB, data.getLastRow(), C_PUB); //B7-Bn
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_PLAT, data.getLastRow(), C_PLAT); //C7-Cn
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_PRINT, data.getLastRow(), C_PRINT); //D7-Dn
		addCheckRange(ReportType.NONBLANK, getDataRow(), C_ONLINE, data.getLastRow(), C_ONLINE); //E7-En

		checkColHeader(data);
		checkGrid(data);
	}
	
	boolean isSupported() {
		return true;
	}
	
	public int getHeadRow() {return 4;}
	public int getTotalRow() {return 5;}
	public int getDataRow() {return 6;}
	
	public int getFirstDataCol() {return 5;}
	public int getLastCol(CounterData data) {return data.getLastCol(getHeadRow());}
	public int getLastDataCol(CounterData data) {return getLastCol(data) - 3;}
	public int getLastDataColWithVal(CounterData data) {return data.getLastCol(getDataRow(), data.getLastCol(getDataRow()) - 3);}
	public int getTotalCol(CounterData data) {return getLastCol(data) - 2;}
	public int getHtmlTotalCol(CounterData data) {return getLastCol(data) - 1;}
	public int getPdfTotalCol(CounterData data) {return getLastCol(data);}
	
	public void checkGrid(CounterData data) {
		addCheckRange(new ColSumCounterCheck(getDataRow(), data.getLastRow(), "Cell should be total of Col"), getTotalRow(), getFirstDataCol(), getTotalRow(), getLastDataColWithVal(data)); 
		addCheckRange(new ColSumCounterCheck(getDataRow(), data.getLastRow(), "Cell should be total of Col"), getTotalRow(), getTotalCol(data), getTotalRow(), getPdfTotalCol(data)); 

		addCheckRange(new IntCounterCheck("Monthly stat must be a number"), getDataRow(), getFirstDataCol(), data.getLastRow(), getLastDataColWithVal(data)); //Month counts
		addCheckRange(ReportType.BLANK, getDataRow(), getLastDataColWithVal(data) + 1, data.getLastRow(), getLastDataCol(data)); //Month counts

		addCheckRange(new RowSumCounterCheck(getFirstDataCol(), getLastDataCol(data), "Cell should be total of data cols"), getDataRow(), getTotalCol(data), data.getLastRow(), getTotalCol(data)); 
		addCheckRange(new IntCounterCheck("HTML total must be a number"), getDataRow(), getHtmlTotalCol(data), data.getLastRow(), getHtmlTotalCol(data)); //HTML counts
		addCheckRange(new IntCounterCheck("PDF total must be a number"), getDataRow(), getPdfTotalCol(data), data.getLastRow(), getPdfTotalCol(data)); //PDF counts
	}
}
