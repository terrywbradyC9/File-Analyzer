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
	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public NameStats create(String key) {return new NameStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(NameStatsItems.class);
	
	private NameStats(String key) {
		super(NameStats.details, key);
	}
	
	public Object compute(File f, FileTest fileTest) {
		sumVal(NameStatsItems.Count, 1);
		sumVal(NameStatsItems.Size, f.length());
		return fileTest.fileTest(f);
	}
}
