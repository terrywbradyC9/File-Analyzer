package gov.nara.nwts.ftapp.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

/**
 * Create FileAnalyzer statistics by directory and by FileType.
 * This routine is useful for understanding the contents of a large volume of material.
 * This routine was built as a sample rule for NWME.
 * @author TBrady
 *
 */
class DirTypeNameMatch extends DirMatch {

	private static enum DirStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Key", 100)),
		Type(StatsItem.makeStringStatsItem("Type", 80)),
		Path(StatsItem.makeStringStatsItem("Path", 300)),
		Count(StatsItem.makeLongStatsItem("Count")),
		CumulativeCount(StatsItem.makeLongStatsItem("Cumulative Count"));
		
		StatsItem si;
		DirStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	
	public static enum Generator implements StatsGenerator {
		INSTANCE;

		class DirTypeStats extends Stats {
			public DirTypeStats(StatsItemConfig config, String key) {
				super(config, key);
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

		public Stats create(String key) {return new DirTypeStats(details, key);}
	}
	
	public static StatsItemConfig details = StatsItemConfig.create(DirStatsItems.class);

	public DirTypeNameMatch(FTDriver dt) {
		super(dt);
	}
	public String toString() {
		return "Count By Type and Dir";
	}
	public String getKey(File f) {
		return getKey(f, f.getParentFile());
	}
	
	public String getKey(File f, Object parentdir) {
		String key = getExt(f);
		if (parentdir instanceof File) {
			key = getExt(f)+": " + ((File)parentdir).getAbsolutePath().substring(getRoot().getAbsolutePath().length());
		}
		return key;		
	}
	
    public String getShortName(){return "Type&Dir";}
    public Stats createStats(String key){  
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details;
    }
	public String getDescription() {
		return "This test counts the number of occurrences of a specific filetype within a directory.  \nA cumulative total is counted for each parent directory.";
	}

}
