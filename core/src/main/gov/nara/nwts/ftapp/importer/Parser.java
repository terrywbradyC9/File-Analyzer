package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.ftprop.FTPropEnum;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class parser to ananlyze and ingest individual rows from a file using a regular expression pattern.
 * NOT USED -- NEED USE CASE OR DELETE
 * @author TBrady
 *
 */
public class Parser extends DefaultImporter {
	public static enum status {PASS,FAIL}
	public static enum Fields {
		NA(0),F1(1),F2(2),F3(3),F4(4),F5(5),F6(6),F7(7),F8(8),F9(9),F10(10);
		int index;
		Fields(int i) {index = i;}
		public String toString() {
			if (this == NA) return "Not Applicable";
			return "Regex Field "+ index;
		}
	} 
	public static enum ParserStatsItems implements StatsItemEnum {
		Row(StatsItem.makeStringStatsItem("Row",60)),
		PassFail(StatsItem.makeEnumStatsItem(status.class, "Pass/Fail").setInitVal(status.PASS)),
		Data(StatsItem.makeStringStatsItem("Data",300));
		
		StatsItem si;
		ParserStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static StatsItemConfig details = StatsItemConfig.create(ParserStatsItems.class);
	Pattern p;
	int cols;
	public StatsItemConfig getDetails() {
		return details;
	}
	public static final String REGEX = "Regular Expression";
	public static final int FIELDS = 10;
	public static final String FIELD_PRE = "Optional Output ";
	public static final String getOptField(int i) {return FIELD_PRE + i;}
	public Parser(FTDriver dt) {
		super(dt);
		this.ftprops.add(new FTPropString(dt, this.getClass().getName(), REGEX, "regex",
				"Regular expression to test against each line of the file", "^(.*)$"));
		for(int i=1; i<=FIELDS; i++) {
			this.ftprops.add(new FTPropEnum(dt, this.getClass().getName(), getOptField(i), "f"+i,
					getOptField(i), Fields.values(), Fields.NA));
		}
	}
	
	public Matcher test(String line) {
		return p.matcher(line);
	}

	public Object getVal(Matcher m, int i) {
		if (m.groupCount()>= i) {
			return m.group(i).trim();
		}
		return "";
	}

	public Object getDefVal(Matcher m, int i, String line) {
		if (m.groupCount()== i) {
			return line;
		}
		return "";
	}

	public Fields getFieldForCol(int i) {
		Fields f = (Fields)getProperty(getOptField(i));
		if (f == null) return Fields.NA;
		return f;
	}
	public void setVals(Matcher m, Stats stats, String line) {
		if (m.matches()) {
			for(int i=1;i<=cols;i++) {
				stats.setKeyVal(details.getByKey(i), getVal(m,getFieldForCol(i).index));
			}
		} else {
			stats.setVal(ParserStatsItems.PassFail, status.FAIL);
			for(int i=1;i<=cols;i++) {
				stats.setKeyVal(details.getByKey(i), getDefVal(m,getFieldForCol(i).index,line));
			}
		}
		
	}

	public ActionResult importFile(File selectedFile) throws IOException {
		String patt = (String)this.getProperty(REGEX);
		details = StatsItemConfig.create(ParserStatsItems.class);
		for(int i=1; i<=FIELDS; i++) {
			Fields f = (Fields)this.getProperty(getOptField(i));
			if (f == Fields.NA) continue;
			details.addStatsItem(i, StatsItem.makeStringStatsItem(f.toString()));
			cols++;
		}
		p = Pattern.compile(patt);
		Timer timer = new Timer();
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(selectedFile));
			int i=1000000;
			for(String line=br.readLine(); line!=null; line=br.readLine()){
				String key = ""+ (i++);
				Stats stats = Stats.Generator.INSTANCE.create(Parser.details, key);
				types.put(key, stats);
				Matcher m = test(line);
				stats.setVal(ParserStatsItems.Data, line);
				setVals(m, stats, line);
			}
			br.close();
			return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), details, types, true, timer.getDuration());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), details, types, false, timer.getDuration());
	}

	public String toString() {
		return "Parser";
	}
	public String getDescription() {
		return "This rule will parse each line of a file and add it to the results table.\n" +
				"This rule requires an understanding of regular expressions.";
	}
	public String getShortName() {
		return "Parse";
	}

}
