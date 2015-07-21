package edu.georgetown.library.fileAnalyzer.filetest;

import java.io.File;

import gov.loc.repository.bagit.Bag;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.ftprop.InitializationStatus;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class VerifyAPTrustBagZip extends VerifyBagZip { 
    public static StatsItemConfig details = StatsItemConfig.create(BagStatsItems.class);

    public VerifyAPTrustBagZip(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Verify APTrust Bag - Zip";
    }
    public String getShortName(){return "Ver APT Zip";}

    
    public Stats createStats(String key){ 
        return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
        return details; 
    }

    public String getDescription() {
        return "This rule will validate the contents of an APTrust bag zip file";
    }
    
    public InitializationStatus init() {
    	details = StatsItemConfig.create(BagStatsItems.class);
    	details.addStatsItem(VerifyAPTrustBag.APT_TITLE, StatsItem.makeStringStatsItem(VerifyAPTrustBag.APT_TITLE));
    	details.addStatsItem(VerifyAPTrustBag.APT_ACCESS, StatsItem.makeStringStatsItem(VerifyAPTrustBag.APT_ACCESS));
    	return new InitializationStatus();
    }
    
    @Override public void validateBagMetadata(Bag bag, File f, Stats stats) {
    	VerifyAPTrustBag.validateAPTrustBagMetadata(bag, f, stats);
    }

}
