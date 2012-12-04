package gov.nara.nwts.ftapp.stats;

import java.io.File;

import gov.nara.nwts.ftapp.filetest.FileTest;

/**
 * Status objects that counts items by key.
 * @author TBrady
 *
 */
public class CountStats extends Stats {
	
	public static enum CountStatsItems implements StatsItemEnum {
		Type(StatsItem.makeStringStatsItem("Type")),
		Count(StatsItem.makeLongStatsItem("Count"));
		
		StatsItem si;
		CountStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static StatsItemConfig details = StatsItemConfig.create(CountStatsItems.class);

	public CountStats(String key) {
		super(key);  
		init(CountStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		sumVal(CountStatsItems.Count, 1);
		return fileTest.fileTest(f);
	}
}
