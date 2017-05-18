package edu.georgetown.library.fileAnalyzer.importer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.importer.DelimitedFileReader;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;
import edu.georgetown.library.fileAnalyzer.util.XMLUtil;

/**
 * @author TBrady
 *
 */
public class ItemUpdateFolderCreate extends DefaultImporter {
	static enum ItemUpdateStatsItems implements StatsItemEnum {
		LineNo(StatsItem.makeStringStatsItem("Line No").setExport(false).setWidth(60)),
        Handle(StatsItem.makeStringStatsItem("Handle")),
		Folder(StatsItem.makeStringStatsItem("Folder")),
		Status(StatsItem.makeEnumStatsItem(status.class, "Status").setWidth(60)),
		Message(StatsItem.makeStringStatsItem("Message", 300).setExport(false))
		;
		
		StatsItem si;
		ItemUpdateStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new Stats(details, key);}
	}
	static StatsItemConfig details = StatsItemConfig.create(ItemUpdateStatsItems.class);
	class CreateException extends Exception {
		private static final long serialVersionUID = 5042987495219000857L;

		CreateException(String s) {
			super(s);
		}
	}
	
	public StatsItemConfig getDetails() {
		return details;
	}
	
	private enum status {INIT,PASS,WARN,FAIL}
	
	NumberFormat nf;
	MetadataRegPropFile metadataPropFile;
	
    public static final String CONTENTS = "contents";
    public static final String DUBLINCORE = "dublin_core.xml";

    public static final String HANDLE_PREFIX = "Handle Prefix";
	public static enum FIXED {
		HANDLE(0), FILE(1), BITDESC(2);
		int index;
		FIXED(int i) {index = i;}
	}
	
	public ItemUpdateFolderCreate(FTDriver dt) {
		super(dt);
		nf = NumberFormat.getNumberInstance();
		nf.setMinimumIntegerDigits(8);
		nf.setGroupingUsed(false);

		this.ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(), HANDLE_PREFIX, "handle",
                "Handle prefix to create match existing item", "http://hdl.handle.net/"));
	}

	public String toString() {
		return "Ingest: Create Item Update Folders";
	}
	public String getDescription() {
		return "This will create folders for a DSpace Item Update task.  \n"+
		        "(This is an experimental feature.  Test carefully before using this.)\n"+
				"\t1) Item Handle - A unique folder will be created for each item to be ingested.  Names must be unique\n"+
				"\t2) Item file name - required, a file with that name must exist relative to the imported spreadsheet\n"+
				"\t3) Bitstream description - Description that will be assigned to a bitstream on ingest\n";
	}
	public String getShortName() {
		return "Item Update Folder";
	}
	
	public void createMetadataFile(Stats stats, File dir, String handle) {
		String filename = DUBLINCORE;
		String schema = "dc";
		
		Document d = XMLUtil.db.newDocument();
		Element e = d.createElement("dublin_core");
		e.setAttribute("schema", schema);
		d.appendChild(e);
		addElement(e, "identifier", "uri", this.getProperty(HANDLE_PREFIX) + handle);

		File f = new File(dir, filename);

		try {
			XMLUtil.doSerialize(d, f);
		} catch (TransformerException|IOException e2) {
			stats.setVal(ItemUpdateStatsItems.Status, status.FAIL);
			stats.setVal(ItemUpdateStatsItems.Message, e2.getMessage());
		}
		
	}

	public void createItem(Stats stats, File selectedFile, Vector<String> cols) {
	    String handle = cols.get(FIXED.HANDLE.index);
	    String file = cols.get(FIXED.FILE.index);
        String bitdesc = cols.get(FIXED.BITDESC.index);
        
        String itemdir = handle.replace("/", "_");
        
        stats.setVal(ItemUpdateStatsItems.Handle, handle);
        stats.setVal(ItemUpdateStatsItems.Folder, itemdir);
        
		File dir = new File(currentIngestDir, itemdir);
		dir.mkdirs();
		File sourceFile = new File(selectedFile.getParentFile(), file);
		if (!sourceFile.exists()) {
            stats.setVal(ItemUpdateStatsItems.Status, status.FAIL);
            stats.setVal(ItemUpdateStatsItems.Message, String.format("File %s not found", file));
            return;		    
		}
		File copyFile = new File(dir, file);
		try {
            Files.copy(sourceFile.toPath(), copyFile.toPath());
        } catch (IOException e) {
            stats.setVal(ItemUpdateStatsItems.Status, status.FAIL);
            stats.setVal(ItemUpdateStatsItems.Message, e.getMessage());
            return;
        }
        createMetadataFile(stats, dir, handle);

        File f = new File(dir, CONTENTS);
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(file);
			bw.write("\t");
			bw.write("bundle:ORIGINAL");
            bw.write("\t");
            bw.write("description:");
            bw.write(bitdesc);
			bw.write("\n");
			
			bw.close();
			
		} catch (IOException e1) {
			stats.setVal(ItemUpdateStatsItems.Status, status.FAIL);
			stats.setVal(ItemUpdateStatsItems.Message, e1.getMessage());
			return;
		}
        stats.setVal(ItemUpdateStatsItems.Status, status.PASS);
		
	}
	
	
	public void addElement(Element e, String name, String qual, String val) {
		Element el = e.getOwnerDocument().createElement("dcvalue");
		e.appendChild(el);
		el.setAttribute("element",name);
		el.setAttribute("qualifier", qual);
		el.appendChild(e.getOwnerDocument().createTextNode(val));
	}
	

	private File currentIngestDir;
	public File getCurrentIngestDir(File selectedFile){
		File parent = selectedFile.getParentFile();
		File defdir = new File(parent, "itemupdate");
		defdir.mkdirs();
		return defdir;
	}
	
	public ActionResult importFile(File selectedFile) throws IOException {
		currentIngestDir = getCurrentIngestDir(selectedFile);
		Timer timer = new Timer();
		Vector<Vector<String>> data = DelimitedFileReader.parseFile(selectedFile, ",");

        TreeMap<String,Stats> types = new TreeMap<String,Stats>();
        int rowKey = 0;
		for(int r=1; r<data.size(); r++) {
			Vector<String> cols = data.get(r);
			String key = nf.format(rowKey++);
			Stats stats = Generator.INSTANCE.create(key);
			this.createItem(stats, selectedFile, cols);
			
			types.put(key, stats);
		}
		
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), getDetails(), types, true, timer.getDuration());
	}
	
	public String validateRowDescription() {
	    return "A title (dc.title) must be present .";
	}

}
