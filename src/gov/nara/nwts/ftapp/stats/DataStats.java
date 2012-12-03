package gov.nara.nwts.ftapp.stats;

import java.io.File;

import gov.nara.nwts.ftapp.filetest.FileTest;

/**
 * Stats object that generically displays a key and data value.  When using this Stats object, the assumption is that the FileTest will customize the column headers.
 * @author TBrady
 *
 */
public class DataStats extends Stats {
	public static enum DataStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Key", 200)),
		Data(StatsItem.makeStatsItem(Object.class, "Data", 300).setInitVal(""));
		
		StatsItem si;
		DataStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static Object[][] details = StatsItem.toObjectArray(DataStatsItems.class);
	
	public DataStats(String key) {
		super(key);
		init(DataStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object o = fileTest.fileTest(f);
		setVal(DataStatsItems.Data, o);
		return o;
	}
}
