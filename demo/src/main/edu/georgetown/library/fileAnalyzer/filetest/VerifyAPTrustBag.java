package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

import java.io.File;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class VerifyAPTrustBag extends VerifyBag { 
    public VerifyAPTrustBag(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Verify APTrust Bag - Dir";
    }
    public String getKey(File f) {
        return this.getRelPath(f);
    }
    
    public String getShortName(){return "Ver APT Dir";}

    public Stats createStats(String key){ 
        return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
        return details; 
    }

    public String getDescription() {
        return "This rule will validate the contents of an APTrust bag directory.\n\n"
        		+ "APTrust directories contain periods in their names.\n"
        		+ "Either start this task at the folder you wish to validate or disable \n"
        		+ "'assume directory names do not contain periods' on the advanced tab.";
    }
    
    @Override public boolean isTestable(File f) {
        return hasBagFile(f);
    }

    
}
