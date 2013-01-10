package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import gov.nara.nwts.ftapp.util.XMLUtil;

import edu.georgetown.library.fileAnalyzer.filetest.IngestValidate.Generator.DSpaceStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
public class IngestValidate extends DefaultFileTest { 
	
	public enum OVERALL_STAT {
		VALID,
		INVALID,
		EXTRA
	}
	public enum CONTENTS {
		ORIGINAL,
		THUMBNAIL
	}
	public enum CONTENTS_STAT {
		MISSING,
		VALID,
		OTHER,
		TAB_MISSING,
		UNLISTED,
		INVALID
	}
	public enum DC_STAT {
		MISSING,
		VALID,
		INVALID
	}
	public enum DATE_STAT {
		VALID,
		INVALID
	}

	public static enum DSpaceStatsItems implements StatsItemEnum {
		ItemFolder(StatsItem.makeStringStatsItem("Item Folder", 200)),
		OverallStat(StatsItem.makeEnumStatsItem(OVERALL_STAT.class, "Status", OVERALL_STAT.INVALID).setWidth(80)),
		NumFiles(StatsItem.makeIntStatsItem("Num Items").setWidth(80)),
		ContentsStat(StatsItem.makeEnumStatsItem(CONTENTS_STAT.class, "Contents File", CONTENTS_STAT.MISSING).setWidth(80)),
		ContentFileCount(StatsItem.makeIntStatsItem("Num ContentF Files").setWidth(80)),
		DublinCoreStat(StatsItem.makeEnumStatsItem(DC_STAT.class, "Dublin Core File", DC_STAT.MISSING).setWidth(80)),
		ItemTitle(StatsItem.makeStringStatsItem("Item Title", 150)),
		Author(StatsItem.makeStringStatsItem("Author", 150)),
		Date(StatsItem.makeStringStatsItem("Date", 80)),
		Language(StatsItem.makeStringStatsItem("Language", 80)),
		Subject(StatsItem.makeStringStatsItem("Subject", 150)),
		Format(StatsItem.makeStringStatsItem("Format", 150)),
		Publisher(StatsItem.makeStringStatsItem("Publisher", 150)),
		PrimaryBitstream(StatsItem.makeStringStatsItem("PrimaryBitstream", 150)),
		Thumbnail(StatsItem.makeStringStatsItem("Thumbnail", 150)),
		License(StatsItem.makeStringStatsItem("License", 150)),
		Text(StatsItem.makeStringStatsItem("TextBitstream", 150)),
		Other(StatsItem.makeStringStatsItem("OtherBitstream", 150)),
		;
		
		StatsItem si;
		DSpaceStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public class DSpaceStats extends Stats {
			
			public DSpaceStats(String key) {
				super(details, key);
			}

			public void setInfo(DSpaceInfo info) {
				setVal(DSpaceStatsItems.OverallStat, info.overall_stat);
				setVal(DSpaceStatsItems.NumFiles, info.files.length);
				setVal(DSpaceStatsItems.ContentsStat, info.contents_stat);
				setVal(DSpaceStatsItems.ContentFileCount, info.contentsList.size());
				setVal(DSpaceStatsItems.DublinCoreStat, info.dc_stat);
				setVal(DSpaceStatsItems.ItemTitle, info.title);
				setVal(DSpaceStatsItems.Author, info.author);
				setVal(DSpaceStatsItems.Date, info.date);
				setVal(DSpaceStatsItems.Language, info.language);
				setVal(DSpaceStatsItems.Subject,info.subject);
				setVal(DSpaceStatsItems.Format,info.format);
				setVal(DSpaceStatsItems.Publisher,info.publisher);
				setVal(DSpaceStatsItems.PrimaryBitstream,info.primary);
				setVal(DSpaceStatsItems.Thumbnail,info.thumbnail);
				setVal(DSpaceStatsItems.License,info.license);
				setVal(DSpaceStatsItems.Text,info.text);
				setVal(DSpaceStatsItems.Other,info.other);
			
			}

		}
		public DSpaceStats create(String key) {return new DSpaceStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(DSpaceStatsItems.class);
	
	public class DSpaceInfo {
		public File contents = null;
		public File dc = null;
		public File[] files;
		public ArrayList<String> contentsList;
		
		public Document d;
		
		public CONTENTS_STAT contents_stat = CONTENTS_STAT.MISSING;
		public DC_STAT dc_stat = DC_STAT.MISSING;
		public OVERALL_STAT overall_stat;
		
		public String title ="";
		public String author ="";
		public String date ="";
		public String language ="";
		public String subject ="";
		public String format ="";
		public String publisher ="";
		public String primary ="";
		public String thumbnail ="";
		public String license ="";
		public String text ="";
		public String other ="";
		
		public DSpaceInfo(File f) {
			files = f.listFiles();
			contentsList = new ArrayList<String>();
			
			for(File file : files) {
				if (file.getName().equals("contents")) {
					contents = file;
					contents_stat = CONTENTS_STAT.VALID;
					readContents(file);
				} else if (file.getName().equals("dublin_core.xml")) {
					dc = file;
					try {
						d = XMLUtil.db.parse(dc);
						dc_stat = DC_STAT.VALID;
						Element root = d.getDocumentElement();
						title = getText(root, "title");
						author = getText(root, "creator");
						date = getText(root, "date");
						language = getText(root, "language");
						subject = getText(root, "subject");
						format = getText(root, "format");
						publisher = getText(root, "publisher");
					} catch (SAXException e) {
						dc_stat = DC_STAT.INVALID;
						e.printStackTrace();
					} catch (IOException e) {
						dc_stat = DC_STAT.INVALID;
						e.printStackTrace();
					}
				}
			}
			for(File file : files) {
				String name = file.getName();
				if (name.equals("contents")) continue;
				if (name.equals("dublin_core.xml")) continue;
				if (name.equals("Thumbs.db")) continue;
				boolean found = false;
				for(String s: contentsList) {
					if (name.equals(s)) found = true;
				}
				if (!found) {
					if (contents_stat == CONTENTS_STAT.VALID) {
						contents_stat = CONTENTS_STAT.UNLISTED;						
					}
					other += name + "; ";
				}
			}
			
			if (contents_stat == CONTENTS_STAT.VALID && dc_stat == DC_STAT.VALID) {
				if (license.equals("") && text.equals("")) {
					overall_stat = OVERALL_STAT.VALID;					
				} else {
					overall_stat = OVERALL_STAT.EXTRA;
				}
			} else if (contents_stat == CONTENTS_STAT.OTHER && dc_stat == DC_STAT.VALID) {
				overall_stat = OVERALL_STAT.EXTRA;
			} else {
				overall_stat = OVERALL_STAT.INVALID;
			}
			
		}
		public String getText(Element root, String tag) {
			NodeList nl = root.getElementsByTagName("dcvalue");
			for(int i=0; i<nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				String att = el.getAttribute("element");
				if (att.equals(tag)) {
					return el.getTextContent();
				}
			}
			return "";
		}
		
		public void readContents(File f) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				for(String s=br.readLine(); s!=null;s=br.readLine()) {
					if (s.trim().equals("")) continue;
					Pattern p = Pattern.compile("^([^\\t]+)\\t(.+)\\s*$");
					Matcher m = p.matcher(s);
					if (m.matches()) {
						String fname = m.group(1);
						contentsList.add(fname);
						String type = m.group(2).trim();
						if (type.equals("bundle:ORIGINAL")) {
							primary = fname;
						} else if (type.equals("bundle:THUMBNAIL")) {
							thumbnail = fname;
						} else if (type.equals("bundle:LICENSE")) {
							license = fname;
						} else if (type.equals("bundle:TEXT")) {
							text = fname;
						} else {
							other += type + "; ";
							contents_stat = CONTENTS_STAT.OTHER;
						}
					} else if (s.indexOf("\t", 0) == -1) {
						contents_stat = CONTENTS_STAT.TAB_MISSING;
						other = "[" + s + "]";
					} else {
						contents_stat = CONTENTS_STAT.INVALID;
						other = "[" + s + "]";
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	long counter = 1000000;
	public IngestValidate(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "Ingest Validate";
	}
	public String getKey(File f) {
		return f.getName();
	}
	
    public String getShortName(){return "Ingest Validate";}

	public Object fileTest(File f) {
		DSpaceStats stats = (DSpaceStats)this.getStats(getKey(f));
		DSpaceInfo info = new DSpaceInfo(f);
		stats.setInfo(info);
		if (info.contents_stat != CONTENTS_STAT.VALID) return info.contents_stat;
		if (info.dc_stat != DC_STAT.VALID) return info.dc_stat;
		return info.files.length;
	}

    public DSpaceStats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details;
    }

    public boolean isTestFiles() {return false;}
    public boolean isTestDirectory() {return true;}

    public String getDescription() {
		return "Analyzes an ingest folder to be uploaded into DSpace.\n\n" +
				"This rule should be pointed at a folder of subfolders where each sub-folder conforms to DSpace's ingest folder conventions.\n" +
				"- An item file should be present\n" +
				"- A thumbnail file may be present.  If present, it must match the item file name + '.jpg'\n" +
				"- An optional license file or text file may be present\n" +
				"- A dublin core metadata file 'dublin_core.xml' is required.  This file will be scanned for required metadata.\n" +
				"- A contents file 'contents' is required.  This file must contain a list of all other required files.\n" +
				"";
	}

}
