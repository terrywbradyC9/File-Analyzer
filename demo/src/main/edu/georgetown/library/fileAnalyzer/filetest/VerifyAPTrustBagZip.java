package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filter.ZipFilter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

import java.io.File;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class VerifyAPTrustBagZip extends VerifyAPTrustBag { 
    public static StatsItemConfig details = StatsItemConfig.create(BagStatsItems.class);

    public VerifyAPTrustBagZip(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Verify APTrust Bag - Zip";
    }
    public String getShortName(){return "Ver APT Zip";}

    
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
        return "This rule will validate the contents of an APTrust bag zip file";
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
    	//TODO validate name
        return true;
    }

	public void initFilters() {
		filters.add(new ZipFilter());
	}
    
}
