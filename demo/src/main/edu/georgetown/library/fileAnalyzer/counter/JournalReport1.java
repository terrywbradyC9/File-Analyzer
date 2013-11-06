package edu.georgetown.library.fileAnalyzer.counter;

public class JournalReport1 extends ReportType {
	public JournalReport1(REV rev) {
		super("Journal Report 1", rev, "Number of Successful Full-text Article Requests by Month and Journal");
	}
	public JournalReport1() {
		this(REV.R3);
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("A2", ReportType.NONBLANK);
		addCheck("A3", new StaticCounterCheck("Date run:"));
		addCheck("A4", ReportType.YYYYMMDD);
		addCheck("A5", ReportType.BLANK);
		addCheck("B5", new StaticCounterCheck("Publisher"));
		addCheck("C5", new StaticCounterCheck("Platform"));
		addCheck("D5", new StaticCounterCheck("Print ISSN"));
		addCheck("E5", new StaticCounterCheck("Online ISSN"));
		
		int R_HEAD = 4;
		int R_TOTAL = 5;
		int R_DATA = 6;
		
		int C_JOURNAL = 0;
		int C_PUB = 1;
		int C_PLAT = 2;
		int C_PRINT = 3;
		int C_ONLINE = 4;
		int C_DATA = 5;		
		int lastCol = data.getLastCol(R_HEAD); 
		int lastColData = data.getLastCol(R_DATA, lastCol -3); 
		int C_DATA_LAST = lastCol - 3;
		int C_TOTAL = lastCol - 2;
		int C_HTML = lastCol - 1;
		int C_PDF = lastCol;
		
		addCheck(R_HEAD, C_TOTAL, new StaticCounterCheck("YTD Total"));
		addCheck(R_HEAD, C_HTML, new StaticCounterCheck("YTD HTML"));
		addCheck(R_HEAD, C_PDF, new StaticCounterCheck("YTD PDF"));
		
		for(int c=C_DATA; c <= C_DATA_LAST; c++) {
			addCheck(R_HEAD, c, ReportType.MMMYYYY);			
		}
		addCheck("A6", new StaticCounterCheck("Total for all journals"));
		addCheck("C6", ReportType.NONBLANK);
		addCheck("D6", ReportType.BLANK);
		addCheck("E6", ReportType.BLANK);

		addCheckRange(ReportType.NONBLANK, R_DATA, C_JOURNAL, data.getLastRow(), C_JOURNAL); //A7-An
		addCheckRange(ReportType.NONBLANK, R_DATA, C_PUB, data.getLastRow(), C_PUB); //B7-Bn
		addCheckRange(ReportType.NONBLANK, R_DATA, C_PLAT, data.getLastRow(), C_PLAT); //C7-Cn
		addCheckRange(ReportType.NONBLANK, R_DATA, C_PRINT, data.getLastRow(), C_PRINT); //D7-Dn
		addCheckRange(ReportType.NONBLANK, R_DATA, C_ONLINE, data.getLastRow(), C_ONLINE); //E7-En

		checkGrid(data, R_DATA, R_TOTAL, C_DATA, lastColData, C_DATA_LAST, C_TOTAL, C_HTML, C_PDF);
	}
	
	boolean isSupported() {
		return true;
	}
	public void checkGrid(CounterData data, int R_DATA, int R_TOTAL, int C_DATA, int lastColData, int C_DATA_LAST, int C_TOTAL, int C_HTML, int C_PDF) {
		addCheckRange(new ColSumCounterCheck(R_DATA, data.getLastRow(), "Cell should be total of Col"), R_TOTAL, C_DATA, R_TOTAL, lastColData); 
		addCheckRange(new ColSumCounterCheck(R_DATA, data.getLastRow(), "Cell should be total of Col"), R_TOTAL, C_TOTAL, R_TOTAL, C_PDF); 

		addCheckRange(new IntCounterCheck("Monthly stat must be a number"), R_DATA, C_DATA, data.getLastRow(), lastColData); //Month counts
		addCheckRange(ReportType.BLANK, R_DATA, lastColData + 1, data.getLastRow(), C_DATA_LAST); //Month counts

		addCheckRange(new RowSumCounterCheck(C_DATA, C_DATA_LAST, "Cell should be total of Col F - prior Col"), R_DATA, C_TOTAL, data.getLastRow(), C_TOTAL); 
		addCheckRange(new IntCounterCheck("HTML total must be a number"), R_DATA, C_HTML, data.getLastRow(), C_HTML); //HTML counts
		addCheckRange(new IntCounterCheck("PDF total must be a number"), R_DATA, C_PDF, data.getLastRow(), C_PDF); //PDF counts
	}
}
