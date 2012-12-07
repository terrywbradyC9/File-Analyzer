package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import java.io.File;

/**
 * Stats object showing file name and file size.
 * @author TBrady
 *
 */
public class DataStats extends Stats {
	private static enum DataStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStatsItem(Object.class, "Key", 200)),
		Data(StatsItem.makeStatsItem(Object.class, "Data", 200));
		
		StatsItem si;
		DataStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public DataStats create(StatsItemConfig config, String key) {return new DataStats(config, key);}
		public DataStats create(String key) {return new DataStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(DataStatsItems.class);
	
	private DataStats(String key) {
		super(DataStats.details, key);
	}
	
	private DataStats(StatsItemConfig config, String key) {
		super(config, key);
	}

	public Object compute(File f, FileTest fileTest) {
		Object o = fileTest.fileTest(f);
		setVal(DataStatsItems.Data, o);
		return o;
	}
}
