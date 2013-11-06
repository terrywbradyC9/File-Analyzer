package edu.georgetown.library.fileAnalyzer.counter;

public class JournalReport1R4 extends JournalReport1 {
	public JournalReport1R4() {
		super(REV.R4);
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("A2", ReportType.NONBLANK);
		//Institutional Identifier, A3 may be blank
		addCheck("A4", new StaticCounterCheck("Period Covered by Report"));
		addCheck("A5", ReportType.YYYYMMDD_to_YYYYMMDD);
		addCheck("A6", new StaticCounterCheck("Date run"));
		addCheck("A7", ReportType.YYYYMMDD);
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
		
		int R_HEAD = 7;
		int R_TOTAL = 8;
		int R_DATA = 9;
		
		int C_JOURNAL = 0;//a
		int C_PUB = 1;//b
		int C_PLAT = 2;//c
		//int C_DOI = 3;//d
		//int C_PROP = 4;//e
		//int C_PRINT = 5;//f
		//int C_ONLINE = 6;//g
		int C_TOTAL = 7;//h
		int C_HTML = 8;//i
		int C_PDF = 9;//j
		int C_DATA = 10;//k...		
		int lastCol = data.getLastCol(R_HEAD); 
		int lastColData = data.getLastCol(R_DATA, lastCol); 
		int C_DATA_LAST = lastCol;
		
		for(int c=C_DATA; c <= C_DATA_LAST; c++) {
			addCheck(R_HEAD, c, ReportType.MMMYYYY);			
		}
		addCheck("A9", new StaticCounterCheck("Total for all journals"));
		//B9, publisher/vendor, may be blank
		addCheck("C9", ReportType.NONBLANK); //platform
		addCheck("D9", ReportType.BLANK);
		addCheck("E9", ReportType.BLANK);
		addCheck("F9", ReportType.BLANK);
		addCheck("G9", ReportType.BLANK);

		addCheckRange(ReportType.NONBLANK, R_DATA, C_JOURNAL, data.getLastRow(), C_JOURNAL); 
		addCheckRange(ReportType.NONBLANK, R_DATA, C_PUB, data.getLastRow(), C_PUB); 
		addCheckRange(ReportType.NONBLANK, R_DATA, C_PLAT, data.getLastRow(), C_PLAT); 
		//addCheckRange(ReportType.NONBLANK, R_DATA, C_DOI, data.getLastRow(), C_DOI); 
		//addCheckRange(ReportType.NONBLANK, R_DATA, C_PROP, data.getLastRow(), C_PROP); 
		//addCheckRange(ReportType.NONBLANK, R_DATA, C_PRINT, data.getLastRow(), C_PRINT); 
		//addCheckRange(ReportType.NONBLANK, R_DATA, C_ONLINE, data.getLastRow(), C_ONLINE); 

		checkGrid(data, R_DATA, R_TOTAL, C_DATA, lastColData, C_DATA_LAST, C_TOTAL, C_HTML, C_PDF);
	}
}
