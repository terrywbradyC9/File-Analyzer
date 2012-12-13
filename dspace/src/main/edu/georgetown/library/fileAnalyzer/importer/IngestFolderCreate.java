package edu.georgetown.library.fileAnalyzer.importer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.importer.DelimitedFileImporter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import gov.nara.nwts.ftapp.util.XMLUtil;

/**
 * @author TBrady
 *
 */
public class IngestFolderCreate extends DefaultImporter {
	private static enum IngestStatsItems implements StatsItemEnum {
		LineNo(StatsItem.makeStringStatsItem("Line No").setExport(false)),
		Status(StatsItem.makeEnumStatsItem(status.class, "Status")),
		Message(StatsItem.makeStringStatsItem("Message").setExport(false))
		;
		
		StatsItem si;
		IngestStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new Stats(details, key);}
	}
	static StatsItemConfig details = StatsItemConfig.create(IngestStatsItems.class);
	class column {
		boolean valid;
		boolean fixed;
		int inputCol;
		String element;
		String qualifier;
		String name;
		
		column(int inputCol, String header) {
			this(inputCol, header, false);
		}
		column(int inputCol, String header, boolean fixed) {
			this.fixed = fixed;
			this.name = header;
			this.inputCol = inputCol;
			String[] parts = header.split("\\.");
			if ((parts.length < 2) || (parts.length > 3)) {
				valid = false;
			} else if (parts[0].equals("dc")) {
				valid = true;
				element = parts[1];
				if (parts.length > 2) {
					qualifier = parts[2];
				} else {
					qualifier = "none";
				}
			} else {
				valid = false;
			}
		}
		
	}
	
	class createStatus {
		status stat;
		String message;
		
		createStatus(status stat, String message) {
			this.stat = stat;
			this.message = message;
		}
		
		createStatus append(createStatus cs) {
			if (cs.stat.ordinal() > status.PASS.ordinal()) {
				return cs;
			}
			
			String s = this.message;
			if (s.equals("")) {
				s = cs.message;
			} else {
				s = s + ".  " + cs.message;
			}
			return new createStatus(cs.stat, s);
		}
	}
	
	Vector<column> colHeaderDefs;
	HashMap<String,column> colByName;
	HashMap<String,Integer> folders;
	
	public StatsItemConfig getDetails() {
		return details;
	}
	
	public enum status {INIT,PASS,WARN,FAIL}
	NumberFormat nf;
	
	public IngestFolderCreate(FTDriver dt) {
		super(dt);
		nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(8);
		nf.setGroupingUsed(false);
	}

	public String toString() {
		return "Ingest: Create Ingest Folders";
	}
	public String getDescription() {
		return "This will create DSpace Ingest Folders.  Note: this action will MOVE files into the ingest folder structure.  Please save a backup of your initial directory before running this action the first time.\n"+
				"The File Test 'Ingest Inventory' can be used to create an initial spreadsheet if one does not exist.\n"+
				"File Structure\n"+
				"\t1) Folder Name - A unique folder will be created for each item to be ingested.  Names must be unique\n"+
				"\t2) Item file name - required, a file with that name must exist relative to the imported spreadsheet\n"+
				"\t3) Thumbnail file name - optional, file must exist is present\n"+
				"\tAddition columns should have a dublin core field name in their header.  Columns without a 'dc.' header will be ignored\n" +
				"A title (dc.title) and a properly formatted creation date (dc.date.created) must be present somewhere in the set of additional columns.";
	}
	public String getShortName() {
		return "Ingest Folder";
	}

	public createStatus createItem(File selectedFile, Vector<String> cols) {
		Document d = XMLUtil.db.newDocument();
		Element e = d.createElement("dublin_core");
		e.setAttribute("schema", "dc");
		d.appendChild(e);
		for(int i=0; i<cols.size(); i++) {
			String col = cols.get(i);
			if (col == "") continue;
			column colhead = colHeaderDefs.get(i);
			if (colhead.valid) {
				addElement(e, colhead.element, colhead.qualifier, col);
			}
		}

		details = StatsItemConfig.create(IngestStatsItems.class); 
		for(column col: colHeaderDefs) {
			if (col.valid || col.fixed) {
				details.addStatsItem(col.inputCol, StatsItem.makeStringStatsItem(col.name, 150));
			}
		}

		StringBuffer buf = new StringBuffer();
		File dir = new File(new File(selectedFile.getParentFile(), "ingest"), cols.get(0));
		dir.mkdirs();
		File f = new File(dir, "dublin_core.xml");
		if (f.exists()) {
			buf.append("dublin_core.xml overwritten. ");
		} else {
			buf.append("dublin_core.xml created. ");			
		}
		XMLUtil.serialize(d, f);

		f = new File(dir, "contents");
		if (f.exists()) {
			buf.append("contents overwritten");
		} else {
			buf.append("contents created");			
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			String val = (new File(cols.get(1))).getName();
			bw.write(val);
			bw.write("\t");
			bw.write("bundle:ORIGINAL");
			bw.write("\n");
			
			val = (new File(cols.get(2))).getName();
			if (!val.isEmpty()) {
				bw.write(val);
				bw.write("\t");
				bw.write("bundle:THUMBNAIL");
				bw.write("\n");				
			}
			bw.close();
			
		} catch (IOException e1) {
			return new createStatus(status.FAIL, e1.getMessage());
		}
		
		return new createStatus(status.PASS, buf.toString());
	}
	
	
	public void addElement(Element e, String name, String qual, String val) {
		Element el = e.getOwnerDocument().createElement("dcvalue");
		e.appendChild(el);
		el.setAttribute("element",name);
		el.setAttribute("qualifier", qual);
		el.appendChild(e.getOwnerDocument().createTextNode(val));
	}
	
	public void addColumn(column col) {
		colHeaderDefs.add(col);
		colByName.put(col.name, col);
	}
	
	public String getColumnValue(Vector<String> vals, String name, String def) {
		column col = colByName.get(name);
		if (col == null) return def;
		if (col.inputCol > vals.size()) return def;
		String val = vals.get(col.inputCol);
		if (val == null) return def;
		if (val.trim().isEmpty()) return def;
		return val.trim();
	}

	public boolean hasColumnValue(Vector<String> vals, String name) {
		return getColumnValue(vals, name, null) != null;
	}

	public ActionResult importFile(File selectedFile) throws IOException {
		Timer timer = new Timer();
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		int rowKey = 0;
		Vector<Vector<String>> data = DelimitedFileImporter.parseFile(selectedFile, "\t");
		folders = new HashMap<String,Integer>();
		colHeaderDefs = new Vector<column>();
		colByName = new HashMap<String, column>();
		Vector<String> colheads = data.get(0);
		
		addColumn(new column(0, "Item Folder", true));
		addColumn(new column(1, "Item File", true));
		addColumn(new column(2, "Item Thumbnail", true));
		
		for(int i=3; i< colheads.size(); i++) {
			String colh = colheads.get(i);
			addColumn(new column(i, colh));
		}
		
		for(int r=1; r<data.size(); r++) {
			Vector<String> cols = data.get(r);
			String key = nf.format(rowKey++);
			Stats stats = Generator.INSTANCE.create(key);
			importRow(selectedFile, cols, stats);
			
			types.put(key, stats);
		}
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), getDetails(), types, true, timer.getDuration());
	}

	public void importRow(File selectedFile, Vector<String> cols,Stats stats) {
		createStatus cs = new createStatus(status.INIT, "");
		stats.setVal(IngestStatsItems.Status, cs.stat);
		stats.setVal(IngestStatsItems.Message, cs.message);
		
		if (cols.size() == colHeaderDefs.size()) {
			String folder = "";
			String file = "";
			String thumb = "";
			
			for(int i=0; i<cols.size(); i++){
				column colhead = colHeaderDefs.get(i);
				String val = cols.get(i);
				if ((colhead.fixed || colhead.valid)) {
					stats.setKeyVal(details.getByKey(i), val);
				}
				
				if ((colhead.fixed) && (cs.stat == status.INIT)) {
					if ((i == 0)) {
						if (val.equals("")) cs = new createStatus(status.FAIL, "Item folder name is required");
						folder = val;
						
						Integer x = folders.get(folder);
						if (x == null) {
							folders.put(folder, 1);								
						} else {
							folders.put(folder, x++);
							cs = new createStatus(status.FAIL, "Item folder name [" + folder + "] is a duplicate of another folder");
						}
					}
					if ((i == 1)) {
						if (val.equals("")) cs = new createStatus(status.FAIL, "Item file name is required");
						file = val;
					}
					if ((i == 2)) {
						thumb = val;
						if (thumb.isEmpty()) {
						} else if (!thumb.equals(file+".jpg")){
							cs = new createStatus(status.FAIL, "DSpace requires thumbnail to be named [" + file + ".jpg]");							
						}
					}
				}
			}				
			
			
			if (cs.stat == status.INIT) {
				cs = testFile(selectedFile, folder, file);
			}
			
			if (cs.stat == status.INIT) {
				if (!thumb.equals("")) cs = cs.append(testFile(selectedFile, folder, thumb));
			}

			if (cs.stat == status.INIT) {
				if (!hasColumnValue(cols, "dc.title")) {
					cs = new createStatus(status.FAIL, "Item must have element [dc.title]");
				} else if (!hasColumnValue(cols, "dc.date.created")) {
					cs = new createStatus(status.FAIL, "Item must have element [dc.date.created]");
				} else {
					for(column col: colHeaderDefs) {
						if (col.valid && col.element.equals("date")) {
							String val = getColumnValue(cols, col.name, "");
							if (!testDate(val)) {
								cs = new createStatus(status.FAIL, col.name +" [" + val + "] must start with either YYYY-MM-DD, YYYY-MM, YYYY or 'No Date'.");
							}
						}
					}
				}
				
			}
			
			if (cs.stat == status.INIT) {
				cs = cs.append(prepFile(selectedFile, folder, file));
				if (!thumb.equals("")) cs = cs.append(prepFile(selectedFile, folder, thumb));
			}

			if (cs.stat == status.INIT) {
				cs = cs.append(createItem(selectedFile, cols));
			}
			
			stats.setVal(IngestStatsItems.Status, cs.stat);
			stats.setVal(IngestStatsItems.Message, cs.message);					
		} else {
			stats.setVal(IngestStatsItems.Status, status.FAIL);
			stats.setVal(IngestStatsItems.Message, ""+cols.size()+" : "+colHeaderDefs.size());
		}
		
    	
    }
	
	
	public boolean testDate(String s) {
		Pattern p = Pattern.compile("^(No Date|\\d\\d\\d\\d-\\d\\d-\\d\\d|\\d\\d\\d\\d-\\d\\d|\\d\\d\\d\\d)( .*)?$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	createStatus testFile(File selectedFile, String folder, String file) {
		File parentFile = selectedFile.getParentFile();
		File ingestDir = new File(parentFile, "ingest");
		File dir = new File(ingestDir, folder);
		File f = new File(parentFile, file);
		File dest = new File(dir, (new File(file)).getName());
		if (dest.exists()) {
			return new createStatus(status.INIT, "");
		} else if (f.exists()) {
			return new createStatus(status.INIT, "");
		} 
		return new createStatus(status.FAIL, "File ["+file+"] does not exist");
	}
	
	createStatus prepFile(File selectedFile, String folder, String file) {
		createStatus cs = new createStatus(status.INIT, "");
		File parentFile = selectedFile.getParentFile();
		File ingestDir = new File(parentFile, "ingest");
		File dir = new File(ingestDir, folder);
		dir.mkdirs();
		File f = new File(parentFile, file);
		File f2 = new File(parentFile.getAbsolutePath() + "\\" + file);
		File dest = new File(dir, (new File(file)).getName());
		if (dest.exists()) {
			return cs;
		} else if (f.exists()) {
			f.renameTo(dest);
		} else if (f2.exists()) {
			f2.renameTo(dest);
		}
		
		if (dest.exists()) {
			cs = new createStatus(status.INIT, "File moved to [" + folder + "] dir");
		} else {
			cs = new createStatus(status.FAIL, "File ["+file+"] does not exist");
		}
		return cs;
	}
}
