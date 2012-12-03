package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import java.io.File;

/**
 * Stats object showing file counts and file sizes.
 * @author TBrady
 *
 */
public class FileCountStats extends Stats {
	public static enum FileCountStatsItems implements StatsItemEnum {
		Type(StatsItem.makeStringStatsItem("Type")),
		Count(StatsItem.makeLongStatsItem("Count")),
		Size(StatsItem.makeLongStatsItem("Size"));
		
		StatsItem si;
		FileCountStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static Object[][] details = StatsItem.toObjectArray(FileCountStatsItems.class);

	public FileCountStats(String key) {
		super(key);
		init(FileCountStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object ret = super.compute(f, fileTest);
		Long count = getLongVal(FileCountStatsItems.Count);
		setVal(FileCountStatsItems.Count, count.longValue()+1);
		Long bytes = getLongVal(FileCountStatsItems.Size);
		setVal(FileCountStatsItems.Size, bytes.longValue()+f.length());
		return ret;
	}


}
