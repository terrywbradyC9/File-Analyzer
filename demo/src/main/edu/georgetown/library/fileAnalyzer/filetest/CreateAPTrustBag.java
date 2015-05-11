package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropInt;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class CreateAPTrustBag extends DefaultFileTest {  
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

	long counter = 1000000;
	
    public static final String P_INSTID = "inst-id";
    public static final String P_ITEMUID = "item-uid";
	public static final String P_SRCORG = "source-org";
    public static final String P_BAGTOTAL = "bag-total";
    public static final String P_BAGCOUNT = "bag-count";
    public static final String P_INTSENDDESC = "internal-sender-desc";
    public static final String P_INTSENDID = "internal-sender-id";
    public static final String P_TITLE = "title";
    public static final String P_ACCESS = "access";
	
    private enum Access {Consortia, Restricted, Institution;}
    private FTPropInt pBagCount = new FTPropInt(dt, this.getClass().getSimpleName(),  P_BAGCOUNT, P_BAGCOUNT,
            "Sequence number within a multi-part bag.  Use 1 if not multi-part", 1);
    private FTPropInt pBagTotal = new FTPropInt(dt, this.getClass().getSimpleName(),  P_BAGTOTAL, P_BAGTOTAL,
            "If this is a multi-part bag, set to the number of parts; otherwise set to 1", 1);
    private FTPropEnum pAccess = new FTPropEnum(dt, this.getClass().getSimpleName(),  P_ACCESS, P_ACCESS,
            "Access condition within APTrust.", Access.values(), Access.Institution);
    
	public CreateAPTrustBag(FTDriver dt) {
		super(dt);
		FTPropString fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_SRCORG, P_SRCORG,
                "This should be the human readable name of the APTrust partner organization.", "");
		fps.setFailOnEmpty(true);
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_INSTID, P_INSTID,
                "Institutional ID.", "");
        fps.setFailOnEmpty(true);
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_ITEMUID, P_ITEMUID,
                "Item UID.", "");
        fps.setFailOnEmpty(true);
        ftprops.add(fps);
        ftprops.add(pBagTotal);
        ftprops.add(pBagCount);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_INTSENDDESC, P_INTSENDDESC,
                "[Optional] Human readable description of the contents of the bag.", "");
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_INTSENDID, P_INTSENDID,
                " [Optional] Internal or alternate identifier used at the senders location.", "");
        ftprops.add(fps);

        fps = new FTPropString(dt, this.getClass().getSimpleName(),  P_TITLE, P_TITLE,
                "Human readable title for searching and listing in APTrust.", "");
        fps.setFailOnEmpty(true);
        ftprops.add(fps);

        ftprops.add(pAccess);
	}

	public String toString() {
		return "Create APTrust Bag";
	}
	public String getKey(File f) {
		return f.getName();
	}
	
    public String getShortName(){return "APTBag";}

	public Object fileTest(File f) {
		Stats s = getStats(f);
		
		String bagCount = String.format("%03d", pBagCount.getIntValue(1));
        String bagTotal = String.format("%03d", pBagTotal.getIntValue(1));
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.getProperty(P_INSTID));
		sb.append(".");
        sb.append(this.getProperty(P_ITEMUID));
        sb.append(".b");
        sb.append(bagCount);
        sb.append(".of");
        sb.append(bagTotal);
		File newBag = new File(f.getParentFile(), sb.toString());
		//exists? 
		s.setVal(BagStatsItems.Bag, newBag.getAbsolutePath());
		BagFactory bf = new BagFactory();
		Bag bag = bf.createBag();

		bag.addFileToPayload(f);
		try {
	        File aptinfo = new File(f, "aptrust-info.txt");
	        BufferedWriter bw = new BufferedWriter(new FileWriter(aptinfo));
	        bw.write(String.format("Title: %s%n", this.getProperty(P_TITLE)));
            bw.write(String.format("Access: %s%n", pAccess.getValue()));
	        bw.close();
	        bag.addFileAsTag(aptinfo);
		    
		    DefaultCompleter comp = new DefaultCompleter(bf);
			
			comp.setGenerateBagInfoTxt(true);
			comp.setUpdateBaggingDate(true);
			comp.setUpdateBagSize(true);
			comp.setUpdatePayloadOxum(true);
			comp.setGenerateTagManifest(false);
			
			bag = comp.complete(bag);

		    BagInfoTxt bit = bag.getBagInfoTxt();
	        bit.addSourceOrganization(this.getProperty(P_SRCORG).toString());
		    bit.addInternalSenderDescription(this.getProperty(P_INTSENDDESC).toString());
		    bit.addInternalSenderIdentifier(this.getProperty(P_INTSENDID).toString());
		    bit.setBagCount(String.format("%03d", Integer.parseInt(pBagCount.getValue().toString())));

			bag.write(new FileSystemWriter(bf), newBag);
			bag.close();
			s.setVal(BagStatsItems.Stat, STAT.VALID);
			s.setVal(BagStatsItems.Count, bag.getPayload().size());
		} catch (IOException e) {
			s.setVal(BagStatsItems.Stat, STAT.ERROR);
			s.setVal(BagStatsItems.Message, e.getMessage());
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
		return "This rule will create a bag according to the APTrust Bag Standards: \n" +
				"https://sites.google.com/a/aptrust.org/aptrust-wiki/technical-documentation/processing-ingest/aptrust-bagit-profile";
	}
	
	@Override public boolean isTestDirectory() {
		return true;
	}

	@Override public boolean isTestable(File f) {
		return f.equals(getRoot());
	}

	@Override public boolean isTestFiles() {
		return false; 
	}
	
	@Override public boolean processRoot() {
		return true;
	}
	
}
