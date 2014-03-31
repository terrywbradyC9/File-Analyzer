package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
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
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class parser to ananlyze and ingest individual rows from a file using a regular expression pattern.
 * NOT USED -- NEED USE CASE OR DELETE
 * @author TBrady
 *
 */
public class MultiParser extends DefaultImporter {
	public static enum status {PASS,FAIL}
	public static enum ParserStatsItems implements StatsItemEnum {
		Row(StatsItem.makeStringStatsItem("Row",60)),
		PassFail(StatsItem.makeEnumStatsItem(status.class, "Pass/Fail").setInitVal(status.PASS)),
		Data(StatsItem.makeStringStatsItem("Data",300));
		
		StatsItem si;
		ParserStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static StatsItemConfig details = StatsItemConfig.create(ParserStatsItems.class);
	public StatsItemConfig getDetails() {
		return details;
	}

	public static final int FIELDS = 10;
	public static final String FIELD_GRP = "RegEx Named Groups";
	public static final String FIELD_RX = "RegEx ";
	public static final String getOptField(int i) {return FIELD_RX + i;}
	public MultiParser(FTDriver dt) {
		super(dt);
		this.ftprops.add(new FTPropString(dt, this.getClass().getName(), FIELD_GRP, FIELD_GRP,
				"Regex Named Groups, Comma Separated", ""));
		for(int i=1; i<=FIELDS; i++) {
			this.ftprops.add(new FTPropString(dt, this.getClass().getName(), getOptField(i), "f"+i,
					getOptField(i), ""));
		}
	}
	
	public ActionResult importFile(File selectedFile) throws IOException {
		String sGrp = (String)this.getProperty(FIELD_GRP);
		String[] groups = sGrp.split(","); 
		details = StatsItemConfig.create(ParserStatsItems.class);
		for(String s: groups) {
			details.addStatsItem(s, StatsItem.makeStringStatsItem(s));
		}
		
		Vector<Pattern> patterns = new Vector<Pattern>();
		
		for(int i=1; i<=FIELDS; i++) {
			String s = (String)this.getProperty(getOptField(i));
			if (s.isEmpty()) continue;
			try {
				patterns.add(Pattern.compile(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Timer timer = new Timer();
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(selectedFile));
			int i=1000000;
			for(String line=br.readLine(); line!=null; line=br.readLine()){
				String key = ""+ (i++);
				Stats stats = Stats.Generator.INSTANCE.create(details, key);
				stats.setVal(ParserStatsItems.PassFail, status.FAIL);
				types.put(key, stats);
				for(Pattern p: patterns) {
					Matcher m = p.matcher(line);
					if (m.matches()) {
						stats.setVal(ParserStatsItems.PassFail, status.PASS);
						for(String s: groups) {
							try {
								StatsItem si = details.getByKey(s);
								stats.setKeyVal(si, m.group(s));
							} catch (Exception e) {
							}
						}
						break;
					}
				}
				stats.setVal(ParserStatsItems.Data, line);
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
		return "Multi Parser";
	}
	public String getDescription() {
		return "This rule will parse each line of a file and add it to the results table.\n" +
				"This rule requires an understanding of regular expressions.";
	}
	public String getShortName() {
		return "MultiParse";
	}

}
