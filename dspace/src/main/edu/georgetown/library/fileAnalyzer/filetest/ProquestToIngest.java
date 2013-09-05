package edu.georgetown.library.fileAnalyzer.filetest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.georgetown.library.fileAnalyzer.proquestXsl.GUProquestURIResolver;
import edu.georgetown.library.fileAnalyzer.proquestXsl.MarcUtil;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.YN;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filter.ZipFilter;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import gov.nara.nwts.ftapp.util.XMLUtil;

public class ProquestToIngest extends DefaultFileTest {
	
	File outdir;

	public enum OVERALL_STAT {
		INIT,
		PASS,
		FAIL,
		REVIEW
	}

	public static enum ProquestStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Zip File Name").setWidth(400)),
		OverallStat(StatsItem.makeEnumStatsItem(OVERALL_STAT.class, "Status", OVERALL_STAT.INIT).setWidth(40)),
		Dept(StatsItem.makeStringStatsItem("Department").setWidth(150)),
		Items(StatsItem.makeIntStatsItem("Num Items")),
		Size(StatsItem.makeLongStatsItem("Total Size")),
		XmlStat(StatsItem.makeEnumStatsItem(OVERALL_STAT.class, "XML Status", OVERALL_STAT.INIT).setWidth(40)),
		EmbargoTerms(StatsItem.makeStringStatsItem("Embargo Terms",80)),
		EmbargoCustom(StatsItem.makeStringStatsItem("Embargo Custom",80)),
		Title(StatsItem.makeStringStatsItem("Title",350)),
		Message(StatsItem.makeStringStatsItem("Status Note", 300)),
		;
		
		StatsItem si;
		ProquestStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {
			return new Stats(details, key);
		}
	}
	static StatsItemConfig details = StatsItemConfig.create(ProquestStatsItems.class);
	static String P_MARC = "Generate MARC";
	static String P_FOLDERS = "Separate Folders per Dept";
	static String PS_FOLDERS = "folders";
	
	public ProquestToIngest(FTDriver dt) {
		super(dt);
		this.ftprops.add(new FTPropEnum(dt, this.getClass().getSimpleName(),  P_MARC, P_MARC,
				"Generate a MARC XML record for QC/troubleshooting purposes", YN.values(), YN.N));	
		this.ftprops.add(new FTPropEnum(dt, this.getClass().getSimpleName(),  P_FOLDERS, PS_FOLDERS,
				"Group ETD's into folders by academic department", YN.values(), YN.Y));	
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_UNIV_NAME, MarcUtil.P_UNIV_NAME,
				"University Name", "My University"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_UNIV_LOC, MarcUtil.P_UNIV_LOC,
				"University Location", "My University Location"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_EMBARGO_SCHEMA, MarcUtil.P_EMBARGO_SCHEMA,
				"Embargo Schema Prefix", "local"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_EMBARGO_ELEMENT, MarcUtil.P_EMBARGO_ELEMENT,
				"Embargo Element", "embargo"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_EMBARGO_TERMS, MarcUtil.P_EMBARGO_TERMS,
				"Embargo Policy Qualifier", "terms"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_EMBARGO_CUSTOM, MarcUtil.P_EMBARGO_CUSTOM,
				"Embargo Custom Date Qualifier", "custom-date"));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  MarcUtil.P_EMBARGO_LIFT, MarcUtil.P_EMBARGO_LIFT,
				"Embargo Lift Date Qualifier", "lift-date"));
	}
	
	public static final String PQEXTRACT = "pqextract_";
	
	public void init() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
		outdir = new File(getRoot(), PQEXTRACT+df.format(new Date()));
		outdir.mkdir();		
	}

	public String getDescription() {
		return "Prepare Proquest Files for DSpace Ingest";
	}
	public String getKey(File f) {
		return f.getAbsolutePath().substring(getRoot().getAbsolutePath().length());
	}
	
	public boolean isTestable(File f) {
		return !getKey(f).contains("\\"+PQEXTRACT);
	}

	public Object fileTest(File f) {
		String key = getKey(f);
		Stats stats = (Stats)this.getStats(key);
		File zout = new File(outdir, f.getName().replaceAll("\\.[zZ][iI][pP]$", ""));
		File revzout = new File(outdir, "review_"+f.getName().replaceAll("\\.[zZ][iI][pP]$", ""));
		zout.mkdir();
		int zcount = 0;
		long bytes = 0;
		byte[] buf = new byte[4096];
		String dept = "TBD";
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
			File contents = new File(zout, "contents");
			BufferedWriter br = new BufferedWriter(new FileWriter(contents));
			
			for(ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()){
				File ztemp = new File(ze.getName());
				File zeout = new File(zout, ztemp.getName());
				if (ze.isDirectory()) continue;
				
				FileOutputStream fos = new FileOutputStream(zeout);
				for(int i=zis.read(buf); i > -1; i=zis.read(buf)) {
					fos.write(buf, 0, i);
					bytes += i;
				}
				fos.flush();
				fos.close();
				
				if (ze.getName().toLowerCase().endsWith(".xml") && (!ze.getName().contains("/"))) {
					try {
						Document d = XMLUtil.db.parse(zeout);
						stats.setVal(ProquestStatsItems.XmlStat, OVERALL_STAT.PASS);
						NodeList nl = d.getElementsByTagName("DISS_title");
						if (nl.getLength() == 1) {
							Element elem = (Element)nl.item(0);
							stats.setVal(ProquestStatsItems.Title, elem.getTextContent());
						}
						nl = d.getElementsByTagName("DISS_inst_contact");
						
						if (nl.getLength() == 1) {
							Element elem = (Element)nl.item(0);
							dept = elem.getTextContent();
							stats.setVal(ProquestStatsItems.Dept, dept);
						}
						
						File dc = new File(zout, "dublin_core.xml");
						XMLUtil.doTransform(d, dc, GUProquestURIResolver.INSTANCE, "proquest2ingest-dc.xsl", MarcUtil.getXslParm(this.ftprops));

						if (this.getProperty(P_MARC).equals(YN.Y)) {
							File marc = new File(zout, "marc.xml");
							XMLUtil.doTransform(d, marc, GUProquestURIResolver.INSTANCE, "proquest2marc.xsl", MarcUtil.getXslParm(this.ftprops));							
						}
						
						if (d.getDocumentElement().hasAttribute("embargo_code")) {
							String s = d.getDocumentElement().getAttribute("embargo_code");
							if (!s.equals("0")) {
								File gu = new File(zout, "metadata_" + getProperty(MarcUtil.P_EMBARGO_SCHEMA, "local") + ".xml");
								XMLUtil.doTransform(d, gu, GUProquestURIResolver.INSTANCE, "proquest2ingest-local.xsl", MarcUtil.getXslParm(this.ftprops));	
								
								Document gd =  XMLUtil.db.parse(gu);
								nl = gd.getElementsByTagName("dcvalue");
								for(int i=0; i<nl.getLength(); i++) {
									Element elem = (Element)nl.item(i);
									if (!elem.getAttribute("element").equals("embargo")) continue;
									if (elem.getAttribute("qualifier").equals("terms")) {
										stats.setVal(ProquestStatsItems.EmbargoTerms, elem.getTextContent());
									} else if (elem.getAttribute("qualifier").equals("custom-date")) {
										stats.setVal(ProquestStatsItems.EmbargoCustom, elem.getTextContent());
									}
								}
							}
						}
					} catch (SAXException e) {
						stats.setVal(ProquestStatsItems.XmlStat, OVERALL_STAT.FAIL);
						stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.FAIL);
						stats.setVal(ProquestStatsItems.Message, e.getMessage());					
					} catch (TransformerException e) {
						stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.FAIL);
						stats.setVal(ProquestStatsItems.Message, e.getMessage());					
					}
				} else {
					br.write(ztemp.getName());
					br.write("\t");
					br.write("bundle:ORIGINAL\n");
				}

				zcount++;

			}
			br.flush();
			br.close();

			zis.close();
			
		} catch (ZipException e) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.FAIL);
			stats.setVal(ProquestStatsItems.Message, e.getMessage());					
		} catch (IOException e) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.FAIL);
			stats.setVal(ProquestStatsItems.Message, e.getMessage());					
		} catch (IllegalArgumentException e) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.FAIL);
			stats.setVal(ProquestStatsItems.Message, "Error unzipping the file perhaps due to unexpected characters in file name.\nProcess manually.");					
		}
		
		stats.setVal(ProquestStatsItems.Items, zcount);
		stats.setVal(ProquestStatsItems.Size, bytes);

		if (bytes > 25000000 && stats.getVal(ProquestStatsItems.OverallStat) == OVERALL_STAT.INIT) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.REVIEW);					
		}
		
		if (zcount > 2 && stats.getVal(ProquestStatsItems.OverallStat) == OVERALL_STAT.INIT) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.REVIEW);					
		} 

		if (zcount == 2 && stats.getVal(ProquestStatsItems.OverallStat) == OVERALL_STAT.INIT) {
			stats.setVal(ProquestStatsItems.OverallStat, OVERALL_STAT.PASS);					
		} 
		
		if (stats.getVal(ProquestStatsItems.OverallStat) == OVERALL_STAT.REVIEW) {
			zout.renameTo(revzout);
			zout = revzout;
		}

		if (this.getProperty(P_FOLDERS).equals(YN.Y)) {
			dept = dept.replaceAll("[^a-zA-Z0-9]", "_").replaceAll("__+","_");
			File deptdir = new File(outdir, dept);
			deptdir.mkdir();
			zout.renameTo(new File(deptdir, zout.getName()));
		}
		return zcount;
	}

	public String getShortName() {
		return "ProQuest";
	}

  	public void initFilters() {
		filters.add(new ZipFilter());
	}

    public Stats createStats(String key){
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details;
    }

	public String toString() {
		return "ProQuest to DSpace Ingest Folder";
	}

}
