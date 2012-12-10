package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.YN;
import gov.nara.nwts.ftapp.filetest.FileTest;
import gov.nara.nwts.ftapp.filetest.NameChecksum;

import java.io.File;

/**
 * Identify items by checksum value, identify duplicate items.
 * @author TBrady
 *
 */
public class ChecksumStats extends Stats {
	
	public static enum ChecksumStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Key", 400)),
		Data(StatsItem.makeStatsItem(Object.class, "Data", 300).setInitVal("")),
		Duplicate(StatsItem.makeEnumStatsItem(YN.class, "Duplicate").setInitVal(YN.N)),
		MatchCount(StatsItem.makeIntStatsItem("Num of Matches").setInitVal(1));
		
		StatsItem si;
		ChecksumStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public ChecksumStats create(String key) {return new ChecksumStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(ChecksumStatsItems.class);
	private ChecksumStats(String key) {
		super(ChecksumStats.details, key);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object o = fileTest.fileTest(f);
		setVal(ChecksumStatsItems.Data, o);
		
		if (fileTest instanceof NameChecksum) {
			if (o != null) {
				((NameChecksum)fileTest).setChecksumKey(o.toString(), this);				
			}
		}
		return o;
	}
}
