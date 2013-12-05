package gov.nara.nwts.ftapp.counterReport;

import gov.nara.nwts.ftapp.counter.REV;
import gov.nara.nwts.ftapp.counter.ReportType;

public class UnknownReport extends ReportType {
	public UnknownReport() {
		super("Unknown", REV.NA, "");
	}
	
	public boolean isSupported() {
		return false;
	}
	public boolean hasTotalRow() {
		return false;
	}

	public static String[] COLS = {""};
	public String[] getCols() {
		return COLS;
	}

}
