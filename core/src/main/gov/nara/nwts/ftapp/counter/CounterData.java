package gov.nara.nwts.ftapp.counter;

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
	

	public void prepFileJStor() {
		if (data.size() > 3) {
			String A1 = getCellValue(Cell.at("A1"));
			String A2 = getCellValue(Cell.at("A2"));
			String A3 = getCellValue(Cell.at("A3"));
			
			//Eliminate JSTOR Leader
			if (A1.equals("Counter Report") && A2.isEmpty() && A3.startsWith("Create time")) {
				data.remove(0);
				data.remove(0);
				data.remove(0);
				fileStat = CheckResult.createFileStatus(CounterStat.JSTOR).setMessage("JSTOR: Remove first 3 lines. ");
			}			
		}
		
		String Last1 = getCellValue(Cell.at(data.size()-1, 0));
		if (Last1 != null) {
			if (Last1.startsWith("©")) {
				data.remove(data.size() - 1);
				
				if (fileStat.stat == CounterStat.JSTOR) {
					fileStat.message = fileStat.message + "JSTOR: Remove blank line before header. ";
				} else {
					fileStat = CheckResult.createFileStatus(CounterStat.JSTOR).setMessage("JSTOR: Remove blank line before header. ");
				}
			}			
		}
		
		//handle JSTOR empty row
		int r = 0;
		for(Vector<String>row: data) {
			boolean isBlank = true;
			for(String c: row) {
				if (c == null) continue;
				if (c.isEmpty()) continue;
				isBlank = false;
				break;
			}
			
			if (isBlank) {
				if ((r==4) || (r==8)) {
					data.remove(r);
					if (fileStat.stat == CounterStat.JSTOR) {
						fileStat.message = fileStat.message + "JSTOR: Remove trailing copyright. ";
					} else {
						fileStat = CheckResult.createFileStatus(CounterStat.JSTOR).setMessage("JSTOR: Remove trailing copyright. ");
					}
					break;					
				}
			}
			r++;
		}
	}
	
	public void shiftCols(ReportType reportType) {
		Vector<String> header = data.get(reportType.getHeadRow());
		boolean bMatch = true;
		boolean bMatch2 = true;
		int col = 0;
		for(String c: reportType.getCols()) {
			String s = (col < header.size()) ? header.get(col) : null;
			String s2 = (col+2 < header.size()) ? header.get(col+2) : null;
			if (!c.equals(s)) bMatch = false;
			if (!c.equals(s2)) bMatch2 = false;
			col++;
		}
		if (bMatch) return;
		if (!bMatch2) return;
		
		fileStat = CheckResult.createFileStatus(CounterStat.SHIFT_2_COL).setMessage("SHIFT: The first 2 cols of data header and rows must be removed");
		
		int r=0;
		for(Vector<String> row : data) {
			if (r >= reportType.getHeadRow()) {
				row.remove(0);
				row.remove(0);
			}
			r++;
		}
	}
	
	public void validate() {
		prepFileJStor();
		ReportType reportType = identifyReportType();
		if (reportType == null) return;
		if (fileStat.stat != CounterStat.VALID && fileStat.stat != CounterStat.UNSUPPORTED_REPORT && fileStat.stat != CounterStat.SHIFT_2_COL) return;
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
		Vector<CounterStat> cstats = new Vector<CounterStat>();
		for(CounterStat st: resultCount.keySet()) {
			cstats.add(0, st);
		}
		for(CounterStat st: cstats) {
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
		} else {
			report = A1;
			fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE).setMessage("No version specified");
			return null;
		}

		String B1 = getCellValue(Cell.at("B1"));
		title += B1;
		
		ReportType reportType = rpt.reportType;
		if (reportType == null) {
			fileStat = CheckResult.createFileStatus(CounterStat.UNKNOWN_REPORT_TYPE);
			return null;
		} 

		shiftCols(reportType);
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
