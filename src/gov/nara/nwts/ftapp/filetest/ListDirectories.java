package gov.nara.nwts.ftapp.filetest;

import java.io.File;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

/**
 * List the full path for a dirctory; this can be used as input for the FileAnalzyer batch capability.
 * @author TBrady
 *
 */
class ListDirectories extends DefaultFileTest {
	public static enum DataStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Folder", 200)),
		Data(StatsItem.makeStatsItem(Object.class, "Name", 300).setInitVal(""));
		
		StatsItem si;
		DataStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	public static StatsItemConfig details = StatsItemConfig.create(DataStatsItems.class);
	public class DataStats extends Stats {
		
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

	public ListDirectories(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "List Dir";
	}

	public Object fileTest(File f) {
		return f.getName();
	}

	public String getKey(File f) {
		String path = f.getAbsolutePath();
		return path;
	}

	public Stats createStats(String key) {
		DataStats stats = new DataStats(key) {
			public Object compute(File f, FileTest fileTest) {
				setVal(DataStatsItems.Data, f.getName());
				return f.getName();
			}
		};
		return stats;
	}

	public StatsItemConfig getStatsDetails() {
		return details;
	}

	public String getShortName() {
		return "DIR";
	}


	public String getDescription() {
		return "Generate a list of directories";
	}

    public boolean isTestDirectory() {
    	return true;
    }
    public boolean isTestFiles() {
    	return false;
    }
}
