package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.YN;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filter.ZipFilter;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropInt;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

import edu.georgetown.library.fileAnalyzer.filetest.CreateAPTrustBag.Access;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class AIPToAPT extends DefaultFileTest { 
	public enum STAT {
		VALID,
		INVALID, 
		ERROR
	}
	
	private static enum BagStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Source", 200)),
		Bag(StatsItem.makeStringStatsItem("Bag", 200)),
		Stat(StatsItem.makeEnumStatsItem(STAT.class, "Bag Status")),
		Count(StatsItem.makeIntStatsItem("Item Count")),
		Message(StatsItem.makeStringStatsItem("Message", 200)),
		;
		StatsItem si;
		BagStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		class BagStats extends Stats {
			public BagStats(String key) {
				super(details, key);
			}

		}
		public BagStats create(String key) {return new BagStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(BagStatsItems.class);

    private FTPropEnum pAccess = new FTPropEnum(dt, this.getClass().getSimpleName(),  P_ACCESS, P_ACCESS,
            "Access condition within APTrust.", Access.values(), Access.Institution);

    public AIPToAPT(FTDriver dt) {
        super(dt);
		FTPropString fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_SRCORG, P_SRCORG,
                "This should be the human readable name of the APTrust partner organization.", "");
		fps.setFailOnEmpty(true);
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_INSTID, P_INSTID,
                "Institutional ID.", "");
        fps.setFailOnEmpty(true);
        ftprops.add(fps);
    }

    public String toString() {
        return "Package AIP for APT";
    }
    public String getShortName(){return "AIP->APT";}
    
    public String getDescription() {
        return "This rule will package a DSpace AIP package for APTrust";
    }
    
    @Override public boolean isTestDirectory() {
    	return false;
    }
    @Override public boolean processRoot() {
        return false;
    }

    @Override public boolean isTestFiles() {
        return true; 
    }

    @Override public boolean isTestable(File f) {
    	return f.getName().toLowerCase().endsWith(".zip");
    }

	public void initFilters() {
		filters.add(new ZipFilter());
	}

	@Override
	public Object fileTest(File f) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
