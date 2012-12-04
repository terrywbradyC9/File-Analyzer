package gov.nara.nwts.ftapp.stats;

import java.io.File;

import gov.nara.nwts.ftapp.filetest.FileTest;

/**
 * Status class showing accumulated counts by directory and by file type.
 * This is useful in helping users discover the contents of a large volume of data.
 * @author TBrady
 *
 */
public class DirTypeStats extends Stats {
	public static enum DirStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Key", 100)),
		Type(StatsItem.makeStringStatsItem("Type", 80)),
		Path(StatsItem.makeStringStatsItem("Path", 300)),
		Count(StatsItem.makeLongStatsItem("Count")),
		CumulativeCount(StatsItem.makeLongStatsItem("Cumulative Count"));
		
		StatsItem si;
		DirStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static StatsItemConfig details = StatsItemConfig.create(DirStatsItems.class);

	public DirTypeStats(String key) {
		super(key);
		init(DirStatsItems.class);
	}
	
	public Object compute(File f, FileTest fileTest) {
		File root = fileTest.getRoot();
		for(File ftest = f.getParentFile(); ftest!=null; ftest = ftest.getParentFile()){
			DirTypeStats stats = (DirTypeStats)fileTest.getStats(fileTest.getKey(f,ftest));
			stats.accumulate(f, fileTest, ftest);
			if (ftest.equals(root)){
				break;
			}
		}
		return fileTest.fileTest(f);
	}
	
	public void accumulate(File f, FileTest fileTest, File parentdir) {
		if (f.getParentFile().equals(parentdir)){
			sumVal(DirStatsItems.Count, 1);
		}
		sumVal(DirStatsItems.CumulativeCount, 1);
		setVal(DirStatsItems.Path, (parentdir==null) ? "" : parentdir.getAbsolutePath().substring(fileTest.getRoot().getAbsolutePath().length()));
		setVal(DirStatsItems.Type, fileTest.getExt(f));		
	}
}
