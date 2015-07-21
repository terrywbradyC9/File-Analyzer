package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.ftprop.InitializationStatus;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag definitions.
 * @author TBrady
 *
 */
class VerifyAPTrustBag extends VerifyBag { 
	public static final String APTRUST_INFO = "aptrust-info.txt";
	public static final String APT_TITLE = "APT Title";
	public static final String APT_ACCESS = "APT Access";
	
    public VerifyAPTrustBag(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Verify APTrust Bag - Dir";
    }
    
    public String getShortName(){return "Ver APT Dir";}

    public String getDescription() {
        return "This rule will validate the contents of an APTrust bag directory.\n\n"
        		+ "APTrust directories contain periods in their names.\n"
        		+ "Either start this task at the folder you wish to validate or disable \n"
        		+ "'assume directory names do not contain periods' on the advanced tab.";
    }
    
    @Override public boolean isTestable(File f) {
        return hasBagFile(f) && hasAptFile(f);
    }

    @Override public void validateBagMetadata(Bag bag, File f, Stats stats) {
    	validateAPTrustBagMetadata(bag, f, stats);
    }
    
    static Pattern pAPT = Pattern.compile("^.+\\..+\\.b(\\d{3,3})\\.of(\\d{3,3})(\\.zip)?$");
    static Pattern pTitle = Pattern.compile("^Title:\\s*(.*)$");
    static Pattern pAccess = Pattern.compile("^Access:\\s*(Consortia|Restricted|Institution)\\s*$");
    
    public InitializationStatus init() {
    	details = StatsItemConfig.create(BagStatsItems.class);
    	details.addStatsItem(APT_TITLE, StatsItem.makeStringStatsItem(APT_TITLE));
    	details.addStatsItem(APT_ACCESS, StatsItem.makeStringStatsItem(APT_ACCESS));
    	return new InitializationStatus();
    }
    public static void validateAPTrustBagMetadata(Bag bag, File f, Stats s) {
	    s.setVal(BagStatsItems.Stat, STAT.VALID);
	    s.setVal(BagStatsItems.Message, "");
    	String source = s.getStringVal(BagStatsItems.BagSourceOrg,"");  	
    	if (source.isEmpty()) {
    	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
    	    s.appendVal(BagStatsItems.Message, "Source Org should not be null. "); 
    	}
    	
    	int count = -1;
    	String scount = s.getStringVal(BagStatsItems.BagCount, "");    	
    	if (scount.isEmpty()) {
    	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
    	    s.appendVal(BagStatsItems.Message, "Bag Count should not be null. "); 
    	} else {
    		try {
				count = Integer.parseInt(scount);
				scount = String.format("%03d", count);
			} catch (NumberFormatException e) {
				scount = "";
	    	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
	    	    s.appendVal(BagStatsItems.Message, "Bag Count should be numeric. "); 
			}
    	}
    	
    	Matcher m = pAPT.matcher(f.getName());
    	if (m.matches()) {
    	    if (!scount.equals(m.group(1))) {
        	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
        	    s.appendVal(BagStatsItems.Message, String.format("Bag count %s mismatch in bag file name %s. ", scount, m.group(1)));     
    	    }
    	} else {
    	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
    	    s.appendVal(BagStatsItems.Message, "APTrust Bags must be named <instid>.<itemid>.b<bag>.of<total> where bag and total are 3 digits. ");     
    	}

    	boolean hasTitle = false;
    	boolean hasAccess = false;
    	boolean hasApt = false;
    	
    	for(BagFile bf: bag.getTags()) {
    		if (bf.getFilepath().equals(APTRUST_INFO)) {
    			hasApt = true;
    			try(InputStream apt = bf.newInputStream()){
    				BufferedReader br = new BufferedReader(new InputStreamReader(apt));
            		for(String line = br.readLine(); line != null; line = br.readLine()) {
            			m = pTitle.matcher(line);
            			if (m.matches()) {
            				if (m.groupCount() >= 1) {
            					if (!m.group(1).isEmpty()) {
            						hasTitle = true;
            						s.setKeyVal(details.getByKey(APT_TITLE), m.group(1));
            					}
            				}
            			} else {
            				m = pAccess.matcher(line);
            				if (m.matches()) {
            					hasAccess = true;
            					s.setKeyVal(details.getByKey(APT_ACCESS), m.group(1));
            				}
            			}
            		}
               	} catch (FileNotFoundException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}    		
    		}
    	}
    	
    	if (hasApt) {
        	if (!hasTitle) {
           	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
        	    s.appendVal(BagStatsItems.Message, String.format("%s file must contain a title. ", APTRUST_INFO));     		    		
        	}

        	if (!hasAccess) {
           	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
        	    s.appendVal(BagStatsItems.Message, String.format("%s must have access set to Consortia, Restricted, or Institution. ", APTRUST_INFO));     		    		
        	}
    	} else {
       	    s.setVal(BagStatsItems.Stat, STAT.INVALID);
    	    s.appendVal(BagStatsItems.Message, String.format("%s file does not exist. ", APTRUST_INFO));     		    		
    	}
    	
    }

    public boolean hasAptFile(File f) {
        return (new File(f, APTRUST_INFO)).exists();
    }

}
