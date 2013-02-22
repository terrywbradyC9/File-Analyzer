package edu.georgetown.library.fileAnalyzer.importer.demo;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.importer.DelimitedFileReader;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import gov.nara.nwts.ftapp.util.XMLUtil;

import edu.georgetown.library.fileAnalyzer.importer.demo.TabSepToDC.Generator.DCStats;


/**
 * Importer for tab delimited files
 * @author TBrady
 *
 */
public class TabSepToDC extends DefaultImporter {

	public enum status {PASS,FAIL}
	NumberFormat nf;
	
	private static enum DCStatsItems implements StatsItemEnum {
		LineNo(StatsItem.makeStringStatsItem("LineNo").setExport(false)),
		Status(StatsItem.makeEnumStatsItem(status.class, "Status")),
		ItemFolder(0, StatsItem.makeStringStatsItem("Item Folder", 200)),
		ItemTitle(1, StatsItem.makeStringStatsItem("Item Title", 150)),
		Author(2, StatsItem.makeStringStatsItem("Author", 150)),
		Date(3, StatsItem.makeStringStatsItem("Date", 80)),
		Language(4, StatsItem.makeStringStatsItem("Language", 80)),
		Subject(5, StatsItem.makeStringStatsItem("Subject", 150)),
		Format(6, StatsItem.makeStringStatsItem("Format", 150)),
		Publsiher(7, StatsItem.makeStringStatsItem("Publisher", 150)),
		;
		
		StatsItem si;
		Integer col;
		DCStatsItems(StatsItem si) {this.si=si;}
		DCStatsItems(int col, StatsItem si) {this.si=si; this.col = col;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		class DCStats extends Stats {
			
			public DCStats(String key) {
				super(details, key);
			}

			public void setColumnVals(Vector<String> cols) {
				for(DCStatsItems dc: DCStatsItems.values()) {
					setColumnVal(dc, cols);
				}
			}
			
			public void setColumnVal(DCStatsItems dc, Vector<String> cols) {
				if (dc.col == null) return;
				if (cols.size() > dc.col) {
					setVal(dc, cols.get(dc.col));
				}
			}
		}
		public DCStats create(String key) {return new DCStats(key);}
	}
	static StatsItemConfig details = StatsItemConfig.create(DCStatsItems.class);

	public TabSepToDC(FTDriver dt) {
		super(dt);
		nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(8);
		nf.setGroupingUsed(false);
	}

	public String getSeparator() {
		return "\t";
	}
	public String toString() {
		return "Demo: Convert Tab Separated File to Dublin Core";
	}
	public String getDescription() {
		return "Prototype: This rule will import a tab separated file and create a Dublin Core File from it.\n" +
				"Thus rule is for illustrative purposes, it does not have an operational purpose.\n" +
				"Expected input format\n" +
				"- Directory name\n" +
				"- Creator\n" +
				"- Title\n" +
				"- Date\n" +
				"- Language\n" +
				"- Subject\n" +
				"- Format\n" +
				"- Publisher";
	}
	public String getShortName() {
		return "Tab2DC";
	}

	public void createItems(Document d, Vector<String> cols) {
		Element e = d.createElement("item");
		e.setAttribute("dir", cols.get(0));
		d.getDocumentElement().appendChild(e);
		addElement(e, "creator", cols.get(2));
		addElement(e, "title", cols.get(1));
		addElement(e, "date", cols.get(3));
		addElement(e, "language", cols.get(4));
		addElement(e, "subject", cols.get(5));
		addElement(e, "format", cols.get(6));
		addElement(e, "publisher", cols.get(7));
	}
	
	public void addElement(Element e, String name, String val) {
		Element el = e.getOwnerDocument().createElement("dcvalue");
		e.appendChild(el);
		el.setAttribute("element",name);
		el.setAttribute("qualifier", "none");
		el.appendChild(e.getOwnerDocument().createTextNode(val));
	}
	
	public ActionResult importFile(File selectedFile) throws IOException {
		Document d = XMLUtil.db.newDocument();
		d.appendChild(d.createElement("items"));
		Timer timer = new Timer();
		DelimitedFileReader dfr = new DelimitedFileReader(selectedFile, getSeparator());
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		int rowKey = 0;
		for(Vector<String> cols = dfr.getRow(); cols!=null; cols=dfr.getRow()){
			String key = nf.format(rowKey++);
			DCStats stats = Generator.INSTANCE.create(key);
			if (cols.size() == 8) {
				stats.setVal(DCStatsItems.Status, status.PASS);
				createItems(d, cols);
			} else {
				stats.setVal(DCStatsItems.Status, status.FAIL);
			}
			
			stats.setColumnVals(cols);
			types.put(key, stats);
		}
		File f = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xml");
		XMLUtil.serialize(d, f);
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), details, types, true, timer.getDuration());
	}
}
