package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Abstract class handling the import of a character-delimited text file allowing for individual values to be wrapped by quotation marks.
 * @author TBrady
 *
 */
public class DelimitedFileImporter extends DefaultImporter {
	private static enum Separator{
		Comma(","),
		Tab("\t"),
		Semicolon(";"),
		Pipe("|");
		
		String separator;
		Separator(String s) {separator = s;}
	}
	public static final String DELIM = "Delimiter";
	public DelimitedFileImporter(FTDriver dt) {
		super(dt);
		this.ftprops.add(new FTPropEnum(dt, this.getClass().getName(), DELIM, "delim",
				"Delimiter character separating fileds", Separator.values(), Separator.Comma));
	}
	boolean forceKey;
	int rowKey = 1000000;
	String currentSeparator;
	
	protected static String getNextString(String in, String sep) {
		return getNextString(in,sep,0);
	}
	protected static String getNextString(String in, String sep, int start) {
		int pos = -1;
		if (in.startsWith("\"")) {
			int qpos = in.indexOf("\"", (start==0) ? 1 : start);
			int qqpos = in.indexOf("\"\"", (start==0) ? 1 : start);
			if ((qpos==qqpos)&&(qqpos >= 0)) {
				return getNextString(in,sep,qqpos+2);
			}
			if (qpos == in.length()) {
				return in;
			}
			if (qpos == -1) {
				qpos = 0;
			}
			pos = in.indexOf(sep,qpos+1);
		} else {
			pos = in.indexOf(sep, 0);
		}
		if (pos == -1) return in;
		return in.substring(0,pos);
	}
	
	public static Vector<String> parseLine(String line, String sep){
		String pline = line;
		Vector<String> cols = new Vector<String>();
		while(pline!=null){
			if (!sep.trim().equals("")) pline = pline.trim();
			String tpline = getNextString(pline,sep);
			cols.add(normalize(tpline));
			if (pline.length() == tpline.length()) break;
			pline = pline.substring(tpline.length()+1);
		}
		return cols;
	}
	
	public static Vector<Vector<String>> parseFile(File f, String sep) throws IOException{
		return parseFile(f,sep,false);
	}
	public static Vector<Vector<String>> parseFile(File f, String sep,boolean skipFirstLine) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		if (skipFirstLine) br.readLine();
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		for(String line=br.readLine(); line!=null; line=br.readLine()){
			rows.add(parseLine(line, sep));
		}
		br.close();
		return rows;
	}
	
	public static String KEY = "key";

	public ActionResult importFile(File selectedFile) throws IOException {
		Separator fileSeparator = (Separator)getProperty(DELIM);
		Timer timer = new Timer();
		forceKey = dt.getImporterForceKey();
		BufferedReader br = new BufferedReader(new FileReader(selectedFile));
		int colcount = 0;
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		StatsItemConfig details = new StatsItemConfig();
		if (forceKey) {
			details.addStatsItem(KEY, StatsItem.makeStringStatsItem("Auto Num").setExport(false));
		}
		
		int colset = 0;
		
		for(String line=br.readLine(); line!=null; line=br.readLine()){
			Vector<String> cols = parseLine(line, fileSeparator.separator);
			colcount = Math.max(colcount,cols.size());
			for(int i=colset; i<colcount; i++) {
				String colkey = "Col"+(i+1);
				details.addStatsItem(colkey, StatsItem.makeStringStatsItem(colkey));
				colset++;
			}
		}
		
		br = new BufferedReader(new FileReader(selectedFile));
		for(String line=br.readLine(); line!=null; line=br.readLine()){
			Vector<String> cols = parseLine(line, fileSeparator.separator);
			String key = cols.get(0);
			if (forceKey) {
				key = "" + (rowKey++);
			} 
			Stats stats = Stats.Generator.INSTANCE.create(key);
			stats.init(details);
			if (forceKey) {
				stats.setKeyVal(details.getByKey(KEY), cols.get(0));
			}
			for(int i=1; i<cols.size(); i++){
				String colkey = "Col"+(i+1);
				stats.setKeyVal(details.getByKey(colkey), cols.get(i));
			}
			types.put(key, stats);
		}
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), details, types, true, timer.getDuration());
	}
	
	protected static String normalize(String val) {
		val = val.trim();
		if (val.startsWith("\"")) {
			val = val.substring(1);
		}
		if (val.endsWith("\"")) {
			val = val.substring(0,val.length()-1);
		}
		if (val.endsWith("'")) {
			val = val.substring(0,val.length()-1);
		}
		return val;
	}
	public boolean allowForceKey() {
		return true;
	}
	public String toString() {
		return "Import Delimited File";
	}
	public String getDescription() {
		return "This rule will import a delimited file (comma separated, tab separated, etc).  Please specify the delimiter character to use.";
	}
	public String getShortName() {
		return "Delim";
	}
}
