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

	String report = "";
	String version = "";
	public RPT rpt;
	public String title = "";
	
	public int getMaxRow() {
		return data.size();
	}
	
	public int getMaxCol(int row) {
		if (data.size() > row) {
			return data.get(row).size()-1;
		}
		return 0;
	}
	
	public int getLastCol(int row) {
		if (data.size() > row) {
			Vector<String> drow = data.get(row);
			return getLastCol(row, drow.size() - 1);
		}
		return 0;
	}

	public int getLastCol(int row, int mcol) {
		if (data.size() > row) {
			Vector<String> drow = data.get(row);
			if (mcol > drow.size()-1) mcol = drow.size() - 1;
			for(int c=mcol; c>=0; c--) {
				String s = drow.get(c);
				if (s == null) continue;
				if (!s.isEmpty()) return c;
			}
		}
		return 0;
	}

	public int getLastRow() {
		for(int r=data.size()-1; r>=0; r--) {
			Vector<String> row = data.get(r);
			if (row == null) continue;
			for(String s: row) {
				if (s == null) continue;
				if (!s.isEmpty()) return r;
			}
		}
		return 0;
	}
	
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
		if (fileStat.stat != CounterStat.VALID && fileStat.stat != CounterStat.UNSUPPORTED_REPORT) return;
		results.addAll(reportType.validate(this));
		TreeMap<CounterStat,Integer> resultCount = new TreeMap<CounterStat,Integer>();
		CounterStat overall = fileStat.stat;
		for(CheckResult cr: results) {
			overall = (cr.stat.ordinal() > overall.ordinal()) ? cr.stat : overall;
			Integer x = resultCount.get(cr.stat);
			x = (x == null) ? 1 : x.intValue() + 1;
			resultCount.put(cr.stat, x);
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
			rpt = RPT.createRPT(report, version);
			if (rpt == null) {
				fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE).setMessage("Unrecognized version: "+version);
				return null;				
			}
			
			title = m.group(4);
			
			if (!m.group(4).isEmpty()) {
				fileStat = CheckResult.createFileStatus(CounterStat.INVALID).setMessage("Extra data in cell A1. ");
			}
		} else {
			report = A1;
			fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE).setMessage("No version specified");
			return null;
		}

		String B1 = getCellValue(Cell.at("B1"));
		title += B1;
		
		ReportType reportType = ReportType.reportTypes.get(rpt);
		if (reportType == null) {
			fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE);
			return null;
		} 

		reportType.init(this);

		if (!reportType.isSupported()) {
			fileStat = CheckResult.createFileStatus(CounterStat.UNSUPPORTED_REPORT);
		}
		return reportType;
	}
	
	public String getCellValue(Cell cell) {
		if (cell.row >= data.size() || cell.row < 0) return null;
		if (cell.col >= data.get(cell.row).size() || cell.col < 0) return null;
		return data.get(cell.row).get(cell.col);
	}
}
