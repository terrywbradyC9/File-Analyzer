package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

import edu.georgetown.library.fileAnalyzer.BAG_TYPE;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class VerifyBag extends DefaultFileTest { 
    public enum STAT {
        VALID,
        INVALID, 
        ERROR,
        NA
    }
    
    private FTPropEnum pBagType = new FTPropEnum(dt, this.getClass().getSimpleName(),  CreateBag.P_BAGTYPE, CreateBag.P_BAGTYPE,
            "Type of bag to create", BAG_TYPE.values(), BAG_TYPE.DIRECTORY);

    static enum BagStatsItems implements StatsItemEnum {
        Key(StatsItem.makeStringStatsItem("Bag Path", 200)),
        Stat(StatsItem.makeEnumStatsItem(STAT.class, "Bag Status")),
        Count(StatsItem.makeIntStatsItem("Item Count")),
        Message(StatsItem.makeStringStatsItem("Message",400)),
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

    long counter = 1000000;
    public VerifyBag(FTDriver dt) {
        super(dt);
        ftprops.add(pBagType);
    }

    public String toString() {
        return "Verify Bag - Dir";
    }
    public String getKey(File f) {
        return this.getRelPath(f);
    }
    
    public String getShortName(){return "Ver Bag";}

    
    public Object fileTest(File f) {
        Stats s = getStats(f);
        BagFactory bf = new BagFactory();
        try (Bag bag = bf.createBag(f);) {
			s.setVal(BagStatsItems.Count, bag.getPayload().size());
			SimpleResult result = bag.verifyValid();
			if (result.isSuccess()) {
			    s.setVal(BagStatsItems.Stat, STAT.VALID);
			} else {
			    s.setVal(BagStatsItems.Stat, STAT.INVALID);
			    for(String m: result.getMessages()) {
			        s.appendVal(BagStatsItems.Message, m +" ");             
			    }
			}
		} catch (Exception e) {
	        s.setVal(BagStatsItems.Message, e.getMessage());
		    s.setVal(BagStatsItems.Stat, STAT.NA);
		}
        return s.getVal(BagStatsItems.Count);
    }
    public Stats createStats(String key){ 
        return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
        return details; 
    }

    public String getDescription() {
        return "This rule will scan for directories with names ending with '_bag'.  BagIt verifyvalid will be run on the bag.";
    }
    
    @Override public boolean isTestDirectory(File f) {
    	return hasDescendant(f, "bagit.txt");
    }
    @Override public boolean processRoot() {
        return true;
    }

    @Override public boolean isTestFiles() {
        return false; 
    }

    public boolean hasBagFile(File f) {
        return (new File(f, "bagit.txt")).exists();
    }
    @Override public boolean isTestable(File f) {
        return hasBagFile(f) && f.getName().endsWith("_bag");
    }

}
