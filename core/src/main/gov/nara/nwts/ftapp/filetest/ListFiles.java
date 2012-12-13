package gov.nara.nwts.ftapp.filetest;

import java.io.File;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

/**
 * List the full path for a dirctory; this can be used as input for the FileAnalzyer batch capability.
 * @author TBrady
 *
 */
class ListFiles extends DefaultFileTest {
	public static enum DataStatsItems implements StatsItemEnum {
		Path(StatsItem.makeStringStatsItem("Folder", 500));
		
		StatsItem si;
		DataStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {
			return new Stats(details, key) {
				public Object compute(File f, FileTest fileTest) {
					Object o = fileTest.fileTest(f);
					setVal(DataStatsItems.Path, o);
					return o;
				}
				
			};
		}
	}
	public static StatsItemConfig details = StatsItemConfig.create(DataStatsItems.class);

	public ListFiles(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "List Files";
	}

	public Object fileTest(File f) {
		return f.getName();
	}

	public String getKey(File f) {
		String path = f.getAbsolutePath();
		return path;
	}

	public Stats createStats(String key) {
		return Generator.INSTANCE.create(key);
	}

	public StatsItemConfig getStatsDetails() {
		return ListFiles.details;
	}

	public String getShortName() {
		return "Files";
	}


	public String getDescription() {
		return "Generate a list of files";
	}

}
