package edu.georgetown.library.fileAnalyzer.counter;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterData {
    Pattern pRptType = Pattern.compile("^(.*?)\\(([^\\)]+)\\)( - )?(.*?)$");
	Vector<Vector<String>> data;
	public ArrayList<CheckResult> results = new ArrayList<CheckResult>();
	
	public CheckResult fileStat = CheckResult.createFileStatus(CounterStat.VALID);

	public String report = "";
	public String version = "";
	public String title = "";
	
	public CounterStat getStat() {
		return fileStat.stat;
	}
	
	public String getMessage() {
		return fileStat.message;
	}
	
	public CounterData(Vector<Vector<String>> data) {
		this.data = data;
	}
	
	
	public void validate() {
		ReportType reportType = identifyReportType();
		if (reportType == null) return;
		if (fileStat.stat != CounterStat.VALID) return;
		results.addAll(reportType.validate(this));
		TreeMap<CounterStat,Integer> resultCount = new TreeMap<CounterStat,Integer>();
		CounterStat overall = CounterStat.VALID;
		for(CheckResult cr: results) {
			overall = (cr.stat.ordinal() > overall.ordinal()) ? cr.stat : overall;
			Integer x = resultCount.get(cr.stat);
			resultCount.put(cr.stat, x == null ? 1 : x++);
		}
		StringBuffer buf = new StringBuffer();
		for(CounterStat st: resultCount.keySet()) {
			buf.append(st.name());
			buf.append(": ");
			buf.append(resultCount.get(st));
			buf.append(" cells; ");
		}
		fileStat = CheckResult.createFileStatus(overall).setMessage(buf.toString());
	}
	

	public ReportType identifyReportType() {
		String A1 = getCellValue(Cell.at("A1"));
		if (A1 == null) {
			fileStat = CheckResult.createFileStatus(CounterStat.INVALID).setMessage("Empty Cell A1");
			return null;
		}
		
		Matcher m = pRptType.matcher(A1);
		if (m.matches()) {
			report = m.group(1).trim();
			version = m.group(2);							
			title = m.group(4);
			
			if (!m.group(4).isEmpty()) {
				fileStat = CheckResult.createFileStatus(CounterStat.INVALID).setMessage("Extra data in cell A1. ");
			}
		} else {
			report = A1;
		}

		String B1 = getCellValue(Cell.at("B1"));
		title += B1;
		
		ReportType reportType = ReportType.reportTypes.get(report);
		if (reportType == null) {
			fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE);
			return null;
		} 
		if (!reportType.isSupported()) {
			fileStat = CheckResult.createFileStatus(CounterStat.UNSUPPORTED_REPORT);
		}
		return reportType;
	}
	
	String getCellValue(Cell cell) {
		if (cell.row >= data.size() || cell.row < 0) return null;
		if (cell.col >= data.get(cell.row).size() || cell.col < 0) return null;
		return data.get(cell.row).get(cell.col);
	}
}
