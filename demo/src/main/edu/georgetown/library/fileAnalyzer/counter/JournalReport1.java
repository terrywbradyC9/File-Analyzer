package edu.georgetown.library.fileAnalyzer.counter;

public class JournalReport1 extends ReportType {
	public JournalReport1() {
		super("Journal Report 1","Number of Successful Full-text Article Requests by Month and Journal");
	}
	
	@Override public void initCustom(CounterData data) {
		addCheck("A2", ReportType.NONBLANK);
		addCheck("A3", new StaticCounterCheck("Date run:"));
		addCheck("A4", ReportType.YYYYMMDD);
		addCheck("A5", ReportType.BLANK);
	}
}
