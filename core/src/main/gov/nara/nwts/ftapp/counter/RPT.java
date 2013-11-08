package gov.nara.nwts.ftapp.counter;

import gov.nara.nwts.ftapp.counterReport.DatabaseReport1;
import gov.nara.nwts.ftapp.counterReport.DatabaseReport1R4;
import gov.nara.nwts.ftapp.counterReport.DatabaseReport3;
import gov.nara.nwts.ftapp.counterReport.JournalReport1;
import gov.nara.nwts.ftapp.counterReport.JournalReport1R4;

/**
 * R3: http://www.projectcounter.org/r3/Release3D9.pdf
 * R4: http://www.projectcounter.org/r4/COPR4.pdf
 * JournalReport1
 * JournalReport1a 
 * JournalReport1GOA (R4)
 * JournalReport2
 * JournalReport3
 * JournalReport4
 * JournalReport5
 * DatabaseReport1
 * DatabaseReport2
 * DatabaseReport3 (R3)
 * PlatformReport1 (R4)
 * BookReport1
 * BookReport2
 * BookReport3
 * BookReport4
 * BookReport5
 * MultimediaReport1
 * ConsortiumReport1
 * ConsortiumReport2
 * ConsortiumReport3
 * 
 * @author twb27
 *
 */
public enum RPT {
	UNKNOWN("",REV.NA, new ReportType("", REV.NA)),
	JR1_R3(JournalReport1.NAME, REV.R3, new JournalReport1()),
	JR1_R4(JournalReport1.NAME, REV.R4, new JournalReport1R4()),
	DR1_R3(DatabaseReport1.NAME, REV.R3, new DatabaseReport1()),
	DR1_R4(DatabaseReport1.NAME, REV.R4, new DatabaseReport1R4()),
	DR3_R3(DatabaseReport3.NAME, REV.R3, new DatabaseReport3()),
	;
	
	public String name;
	public REV rev;
	public ReportType reportType;
	
	RPT(String name, REV rev, ReportType reportType) {
		this.name = name;
		this.rev = rev;
		this.reportType = reportType;
	}

	static RPT createRPT(String name, REV rev) {
		for(RPT rpt: RPT.values()) {
			if (rpt.name.equals(name) && rpt.rev == rev) return rpt;
		}
		return null;
	}

	static RPT createRPT(String name, String rev) {
		for(RPT rpt: RPT.values()) {
			if (rpt.name.equals(name) && rpt.rev.name().equals(rev)) return rpt;
		}
		return null;
	}
}
