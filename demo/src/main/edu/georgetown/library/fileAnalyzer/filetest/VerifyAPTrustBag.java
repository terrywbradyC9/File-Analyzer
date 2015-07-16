package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class VerifyAPTrustBag extends DefaultFileTest { 
    public enum STAT {
        VALID,
        INVALID, 
        ERROR
    }
    
    private static enum BagStatsItems implements StatsItemEnum {
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
    public VerifyAPTrustBag(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Verify APTrust Bag";
    }
    public String getKey(File f) {
        return this.getRelPath(f);
    }
    
    public String getShortName(){return "Ver APT";}

    
    public Object fileTest(File f) {
        Stats s = getStats(f);
        BagFactory bf = new BagFactory();
        Bag bag = bf.createBag(f);
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
        return s.getVal(BagStatsItems.Count);
    }
    public Stats createStats(String key){ 
        return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
        return details; 
    }

    public String getDescription() {
        return "This rule will validate the contents of an APTrust bag file.";
    }
    
    @Override public boolean isTestDirectory() {
        return true;
    }
    @Override public boolean processRoot() {
        return true;
    }

    @Override public boolean isTestFiles() {
        return false; 
    }
    @Override public boolean isTestable(File f) {
        return (new File(f, "bagit.txt")).exists();
    }
}
