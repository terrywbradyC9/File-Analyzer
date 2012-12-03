package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import java.io.File;

/**
 * Stats object showing file name and file size.
 * @author TBrady
 *
 */
public class NameStats extends Stats {
	public static enum NameStatsItems implements StatsItemEnum {
		Name(StatsItem.makeStringStatsItem("Name")),
		Size(StatsItem.makeLongStatsItem("Size"));
		
		StatsItem si;
		NameStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	public static Object[][] details = StatsItem.toObjectArray(NameStatsItems.class);
	
	public NameStats(String key) {
		super(key);
		init(NameStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		sumVal(NameStatsItems.Size, f.length());
		return fileTest.fileTest(f);
	}
}
