package gov.nara.nwts.ftapp.stats;

import gov.nara.nwts.ftapp.filetest.FileTest;

import gov.nara.nwts.ftapp.nameValidation.RenamePassFail;
import gov.nara.nwts.ftapp.crud.CRUD;

import java.io.File;

/**
 * Stats object that reports on database ingest actions.
 * @author TBrady
 *
 */
public class CRUDStats extends Stats {

	public static enum CRUDStatsItems implements StatsItemEnum {
		Path(StatsItem.makeStringStatsItem("Path",450)),
		PassFail(StatsItem.makeEnumStatsItem(RenamePassFail.class, "Pass/Fail").setWidth(50)),
		Status(StatsItem.makeEnumStatsItem(CRUD.class, "Status").setWidth(150));
		
		StatsItem si;
		CRUDStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static StatsItemConfig details = StatsItemConfig.create(CRUDStatsItems.class);

	public CRUDStats(String key) {
		super(key);
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object ret = fileTest.fileTest(f);
		return ret;
	}

	
}
