package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
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
	public enum status {PASS,FAIL}
	
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
	StatsItemConfig mydetails;
	
	public StatsItemConfig getDetails() {
		return details;
	}
	public Pattern getPattern() {
		return Pattern.compile("^(.*)$");
	}

	public Parser(FTDriver dt) {
		super(dt);
		mydetails = getDetails();
		cols = mydetails.size() - 1;
		p = getPattern();
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

	public void setVals(Matcher m, Stats stats, String line) {
		if (m.matches()) {
			for(int i=1;i<=cols;i++) {
				stats.setKeyVal(details.getByKey(i), getVal(m,i));
			}
		} else {
			stats.setVal(ParserStatsItems.PassFail, status.FAIL);
			for(int i=1;i<=cols;i++) {
				stats.setKeyVal(details.getByKey(i), getDefVal(m,i,line));
			}
		}
		
	}

	public ActionResult importFile(File selectedFile) throws IOException {
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
				setVals(m, stats, line);
			}
			br.close();
			return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), mydetails, types, true, timer.getDuration());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), mydetails, types, false, timer.getDuration());
	}

	public String toString() {
		return "Parser";
	}
	public String getDescription() {
		return "This rule will parse each line of a file and add it to the results table.";
	}
	public String getShortName() {
		return "Parse";
	}

}
