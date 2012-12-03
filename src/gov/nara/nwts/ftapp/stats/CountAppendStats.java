package gov.nara.nwts.ftapp.stats;

import java.io.File;

import gov.nara.nwts.ftapp.filetest.FileTest;

/**
 * Stats class that accumulates counts as well as accumulates notes into a text buffer as processing continues.
 * When using a checksum algorithm to find duplicate values, this routine will provide details about instances in which more than one file share the same checksum.
 * @author TBrady
 *
 */
public class CountAppendStats extends Stats {
	public static enum CountAppendStatsItems implements StatsItemEnum {
		Type(StatsItem.makeStringStatsItem("Type")),
		Count(StatsItem.makeLongStatsItem("Count")),
		Details(StatsItem.makeStringStatsItem("Details",2000));
		
		StatsItem si;
		CountAppendStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static Object[][] details = StatsItem.toObjectArray(CountAppendStatsItems.class);
	
	public CountAppendStats(String key) {
		super(key);
		init(CountAppendStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Long count = getLongVal(CountAppendStatsItems.Count);
		setVal(CountAppendStatsItems.Count, count.longValue()+1);
		setVal(CountAppendStatsItems.Details, getVal(CountAppendStatsItems.Details) + f.getAbsolutePath()+"; ");
		return super.compute(f, fileTest);
	}
}
