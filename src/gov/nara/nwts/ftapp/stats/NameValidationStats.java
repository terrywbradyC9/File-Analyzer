package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;
import gov.nara.nwts.ftapp.nameValidation.RenameDetails;
import gov.nara.nwts.ftapp.nameValidation.RenameStatus;
import gov.nara.nwts.ftapp.nameValidation.RenamePassFail;

import java.io.File;

/**
 * Stats object displaying the results of a filename test.
 * @linkplain gov.nara.nwts.ftapp.filetest.NameValidationTest} contains the base logic that makes use of this Stats object.
 * @author TBrady
 *
 */
public class NameValidationStats extends Stats {
	private static enum NameValidationStatsItems implements StatsItemEnum {
		Path(StatsItem.makeStringStatsItem("Path", 450)),
		PassFail(StatsItem.makeEnumStatsItem(RenamePassFail.class, "Pass/Fail").setWidth(50)),
		Status(StatsItem.makeEnumStatsItem(RenameStatus.class,"Status").setWidth(150)),
		Message(StatsItem.makeStringStatsItem("Message", 250)),
		RecommendedPath(StatsItem.makeStringStatsItem("Path", 450));
		
		StatsItem si;
		NameValidationStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new NameValidationStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(NameValidationStatsItems.class);

	private NameValidationStats(String key) {
		super(NameValidationStats.details, key);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object ret = fileTest.fileTest(f);
		if (ret instanceof RenameDetails) {
			RenameDetails rdet = (RenameDetails)ret;
			setVal(NameValidationStatsItems.PassFail, rdet.getPassFail());
			setVal(NameValidationStatsItems.Status, rdet.getRenameStatus());
			setVal(NameValidationStatsItems.Message, rdet.getMessage());
			setVal(NameValidationStatsItems.RecommendedPath, rdet.getDetailNote(fileTest.getRoot()));
		}
		return ret;
	}

	
}
