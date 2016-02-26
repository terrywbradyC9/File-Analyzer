package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.ftprop.InitializationStatus;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.georgetown.library.fileAnalyzer.util.APTrustHelper.Access;
import edu.georgetown.library.fileAnalyzer.stats.BagStatsItems;
import edu.georgetown.library.fileAnalyzer.util.APTrustHelper.STAT;
import edu.georgetown.library.fileAnalyzer.util.IncompleteSettingsException;
import edu.georgetown.library.fileAnalyzer.util.InvalidMetadataException;
import edu.georgetown.library.fileAnalyzer.util.APTrustHelper;


/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
abstract class AIPToAPT extends DefaultFileTest { 
	
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

	public Stats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details; 
    }

    private FTPropEnum pAccess = new FTPropEnum(dt, this.getClass().getSimpleName(),  APTrustHelper.P_ACCESS, APTrustHelper.P_ACCESS,
            "Access condition within APTrust.", Access.values(), Access.Institution);

    public AIPToAPT(FTDriver dt) {
        super(dt);
		FTPropString fps = new FTPropString(dt, this.getClass().getSimpleName(),  APTrustHelper.P_SRCORG, APTrustHelper.P_SRCORG,
                "This should be the human readable name of the APTrust partner organization.", "");
		fps.setFailOnEmpty(true);
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  APTrustHelper.P_INSTID, APTrustHelper.P_INSTID,
                "Institutional ID.", "");
        fps.setFailOnEmpty(true);
        ftprops.add(fps);
    }

	public String getKey(File f) {
		return f.getName();
	}

	File outdir;

	public static final String AIPEXTRACT = "aipextract_";
	public static final String METSXML = "mets.xml";
	
	@Override public InitializationStatus init() {
		InitializationStatus istat = super.init();
		try {
			Path outpath = Files.createTempDirectory(AIPEXTRACT);
			outdir = outpath.toFile();
			outdir.deleteOnExit();
		} catch (IOException e) {
			istat.addFailMessage(e.getMessage());
		}
		return istat;
	}
	
	@Override public void cleanup(int count) {
		if (outdir != null) {
			outdir.delete();
		}
	}
	
    abstract public int fillBag(File f, APTrustHelper aptHelper) throws FileNotFoundException, IOException, InvalidMetadataException;
	
	@Override
	public Object fileTest(File f) {
		Stats stat = getStats(f);
		APTrustHelper aptHelper = new APTrustHelper(f);
		aptHelper.setAccessType((Access)pAccess.getValue());
		aptHelper.setInstitutionId(this.getProperty(APTrustHelper.P_INSTID).toString());
		aptHelper.setSourceOrg(this.getProperty(APTrustHelper.P_SRCORG).toString());
		aptHelper.setBagCount(1);
		aptHelper.setBagTotal(1);
		
		try {
			int count = fillBag(f, aptHelper);
			stat.setVal(BagStatsItems.Count, count);
			aptHelper.createBagFile();
			aptHelper.generateBagInfoFiles();
			aptHelper.writeBagFile();
			stat.setVal(BagStatsItems.Bag, aptHelper.getFinalBagName());
			stat.setVal(BagStatsItems.Stat, STAT.VALID);
		} catch (FileNotFoundException e) {
			stat.setVal(BagStatsItems.Stat, STAT.ERROR);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			stat.setVal(BagStatsItems.Stat, STAT.ERROR);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IncompleteSettingsException e) {
			stat.setVal(BagStatsItems.Stat, STAT.INVALID);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (InvalidMetadataException e) {
			stat.setVal(BagStatsItems.Stat, STAT.INVALID);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}
	    
}
