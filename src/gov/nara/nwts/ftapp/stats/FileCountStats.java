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
	
	public static StatsItemConfig details = StatsItemConfig.create(FileCountStatsItems.class);

	public FileCountStats(String key) {
		super(key);
		init(details);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object ret = super.compute(f, fileTest);
		sumVal(FileCountStatsItems.Count, 1);
		sumVal(FileCountStatsItems.Size, f.length());
		return ret;
	}


}
