package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import java.io.File;

/**
 * Stats object showing file name and file size.
 * @author TBrady
 *
 */
public class NameStats extends Stats {
	private static enum NameStatsItems implements StatsItemEnum {
		Name(StatsItem.makeStringStatsItem("Name")),
		Count(StatsItem.makeLongStatsItem("Count")),
		Size(StatsItem.makeLongStatsItem("Size"));
		
		StatsItem si;
		NameStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	public static StatsItemConfig details = StatsItemConfig.create(NameStatsItems.class);
	
	public NameStats(String key) {
		super(key);
		init(details);
	}
	
	public Object compute(File f, FileTest fileTest) {
		sumVal(NameStatsItems.Count, 1);
		sumVal(NameStatsItems.Size, f.length());
		return fileTest.fileTest(f);
	}
}
