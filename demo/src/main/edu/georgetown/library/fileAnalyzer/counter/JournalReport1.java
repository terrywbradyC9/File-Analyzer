package edu.georgetown.library.fileAnalyzer.counter;

public class JournalReport1 extends ReportType {
	public JournalReport1() {
		super("Journal Report 1","Number of Successful Full-text Article Requests by Month and Journal");
		addCheck(
			"B2",
			new StaticCounterCheck("").setCounterStat(CounterStat.FIXABLE)
		);
	}
}
