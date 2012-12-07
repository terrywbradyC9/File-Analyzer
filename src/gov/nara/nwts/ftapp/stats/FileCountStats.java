package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import java.io.File;

/**
 * Stats object showing file counts and file sizes.
 * @author TBrady
 *
 */
public class FileCountStats extends Stats {
	private static enum FileCountStatsItems implements StatsItemEnum {
		Type(StatsItem.makeStringStatsItem("Type")),
		Count(StatsItem.makeLongStatsItem("Count")),
		Size(StatsItem.makeLongStatsItem("Size"));
		
		StatsItem si;
		FileCountStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public FileCountStats create(String key) {return new FileCountStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(FileCountStatsItems.class);

	private FileCountStats(String key) {
		super(FileCountStats.details, key);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object ret = super.compute(f, fileTest);
		sumVal(FileCountStatsItems.Count, 1);
		sumVal(FileCountStatsItems.Size, f.length());
		return ret;
	}


}
