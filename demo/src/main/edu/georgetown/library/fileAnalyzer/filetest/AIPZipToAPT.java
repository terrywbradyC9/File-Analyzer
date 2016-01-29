package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filter.ZipFilter;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.ftprop.InitializationStatus;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import gov.nara.nwts.ftapp.util.XMLUtil;
import gov.nara.nwts.ftapp.util.XMLUtil.SimpleNamespaceContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.georgetown.library.fileAnalyzer.util.APTrustHelper.Access;
import edu.georgetown.library.fileAnalyzer.util.FABagHelper.IncompleteSettingsExcpetion;
import edu.georgetown.library.fileAnalyzer.util.APTrustHelper;


/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class AIPZipToAPT extends DefaultFileTest { 
	public enum STAT {
		VALID,
		INVALID, 
		ERROR
	}
	
	private static enum BagStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Source", 300)),
		Bag(StatsItem.makeStringStatsItem("Bag", 300)),
		Stat(StatsItem.makeEnumStatsItem(STAT.class, "Bag Status")),
		Count(StatsItem.makeIntStatsItem("Item Count")),
		Message(StatsItem.makeStringStatsItem("Message", 400)),
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

	public Stats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details; 
    }

    private FTPropEnum pAccess = new FTPropEnum(dt, this.getClass().getSimpleName(),  APTrustHelper.P_ACCESS, APTrustHelper.P_ACCESS,
            "Access condition within APTrust.", Access.values(), Access.Institution);

    public AIPZipToAPT(FTDriver dt) {
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
    public String toString() {
        return "Package AIP for APT";
    }
    public String getShortName(){return "AIP Zip->APT";}
    
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
	
	@Override
	public Object fileTest(File f) {
		Stats stat = getStats(f);
		byte[] buf = new byte[4096];
		APTrustHelper aptHelper = new APTrustHelper(f);
		aptHelper.setAccessType((Access)pAccess.getValue());
		aptHelper.setInstitutionId(this.getProperty(APTrustHelper.P_INSTID).toString());
		aptHelper.setSourceOrg(this.getProperty(APTrustHelper.P_SRCORG).toString());
		aptHelper.setBagCount(1);
		aptHelper.setBagTotal(1);
		
		//File zout = new File(outdir, f.getName().replaceAll("\\.[zZ][iI][pP]$", ""));
		File zout = outdir;
		Bag bag = aptHelper.getBag();

		try(
			ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
		) {
			for(ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()){
				if (ze.getName().startsWith("__MACOSX")) continue;
				if (ze.getName().endsWith(".DS_Store")) continue;
				
				File ztemp = new File(ze.getName());
				File zeout = new File(zout, ztemp.getName());
				if (ze.isDirectory()) continue;
				
				try(FileOutputStream fos = new FileOutputStream(zeout)) {
					for(int i=zis.read(buf); i > -1; i=zis.read(buf)) {
						fos.write(buf, 0, i);
					}
				}
				
				if (ze.getName().equals(METSXML)) {
					parseMetsFile(aptHelper, zeout);
				}
				
				bag.addFileToPayload(zeout);
				stat.sumVal(BagStatsItems.Count, 1);
			}
			
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
		} catch (IncompleteSettingsExcpetion e) {
			stat.setVal(BagStatsItems.Stat, STAT.INVALID);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			stat.setVal(BagStatsItems.Stat, STAT.ERROR);
			stat.setVal(BagStatsItems.Message, e.getLocalizedMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			stat.setVal(BagStatsItems.Stat, STAT.ERROR);
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

	
	class InvalidMetadataException extends Exception {
		private static final long serialVersionUID = 1L;

		InvalidMetadataException(String s) {
			super(s);
		}
	}
	
	private void parseMetsFile(APTrustHelper aptHelper, File zeout) throws SAXException, IOException, XPathExpressionException, InvalidMetadataException {
		Document doc = XMLUtil.db_ns.parse(zeout);
		String id = doc.getDocumentElement().getAttribute("OBJID");
		if (id == null) throw new InvalidMetadataException("mets.xml root element must have an OBJID field");
		if (id.isEmpty()) throw new InvalidMetadataException("mets.xml root element must not have an empty OBJID field");
		aptHelper.setInstitutionalSenderId(id);
		
		aptHelper.setItemIdentifer(id.replaceFirst("hdl:", "").replaceFirst("/", "_"));
		
		XPath xp = XMLUtil.xf.newXPath();
		SimpleNamespaceContext nsContext = new XMLUtil().new SimpleNamespaceContext();
		nsContext.add("mods", "http://www.loc.gov/mods/v3");
		xp.setNamespaceContext(nsContext);
		
		String title = xp.evaluate("//mods:title", doc);
		aptHelper.setTitle(title);
		String intSendDesc = xp.evaluate("mods:abstract", doc);
		aptHelper.setInstitutionalSenderDesc(intSendDesc);
		
	}
    
}
