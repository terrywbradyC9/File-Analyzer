package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.ftprop.FTPropFile;
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

	public static final String F_PARSE = "Parser Rule File";
	private FTPropFile pParseRule;
	
	public MultiParser(FTDriver dt) {
		super(dt);
		pParseRule = new FTPropFile(dt, this.getClass().getName(), F_PARSE, F_PARSE,
				"Parser Rule File", "");
		this.ftprops.add(pParseRule);
	}
	
	enum ParserFileMode {NA,COLS,FILTERS,PATTS;}
	
	class ParserFile {
		String[] groups = new String[0];
		String[] fgroups = new String[0];
		Vector<Pattern> patterns = new Vector<Pattern>();
		
		ParserFile(File f) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				ParserFileMode pfm = ParserFileMode.NA;
				for(String line=br.readLine(); line!=null; line=br.readLine()) {
					line = line.trim();
					if (line.startsWith("#")) continue;
					if (line.isEmpty()) continue;
					
					if (line.equals("[COLS]")) {
						pfm = ParserFileMode.COLS;
						continue;
					} 
					if (line.equals("[FILTERS]")) {
						pfm = ParserFileMode.FILTERS;
						continue;
					} 
					if (line.equals("[PATTERNS]")) {
						pfm = ParserFileMode.PATTS;
						continue;
					}
					
					if (pfm == ParserFileMode.COLS) {
						groups = line.split(",");
					} else if (pfm == ParserFileMode.FILTERS) {
						fgroups = line.split(",");
					} else if (pfm == ParserFileMode.PATTS) {
						try {
							patterns.add(Pattern.compile(line));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}
	}
	
	
	public ActionResult importFile(File selectedFile) throws IOException {
		File parse = pParseRule.getFile();
		ParserFile pf = new ParserFile(parse);
		details = StatsItemConfig.create(ParserStatsItems.class);
		for(String grp: pf.groups) {
			details.addStatsItem(grp, StatsItem.makeStringStatsItem(grp));
		}
		for(String grp: pf.fgroups) {
			StatsItem si = details.getByKey(grp);
			if (si != null) {
				si.makeFilter(true);
			}
		}
		for(Pattern patt: pf.patterns) {
			System.out.println(patt.toString());
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
				for(Pattern p: pf.patterns) {
					Matcher m = p.matcher(line);
					if (m.matches()) {
						stats.setVal(ParserStatsItems.PassFail, status.PASS);
						for(String s: pf.groups) {
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
			details.createFilters(types);
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
				"This rule takes advantage of named groups in a regular expression match.\n\n" +
				"[COLS]\n" +
				"FIRST,LAST,ID,COST\n\n" +
				"[FILTERS]\n" +
				"LAST,COST\n\n" +
				"[PATTERNS]\n" +
				"# Sample Comment 1\n" +
				"^(?<FIRST>[^\\t\\-]+)-(?<ID>[^\\t]+)\\t(?<LAST>[^\\t]+).*\\$(?<COST>\\d+).*$\n" +
				"^(?<FIRST>[^\\t\\-]+)-(?<ID>[^\\t]+)\\t(?<LAST>[^\\t]+).*$\n" +
				"# Sample Comment 2\n" +
				"^(?<FIRST>[^\\t\\-]+)\\t(?<LAST>[^\\t]+).*\\$(?<COST>\\d+).*$\n" +
				"^(?<FIRST>[^\\t\\-]+)\\t(?<LAST>[^\\t]+).*$\n" +
				"^$";
	}
	public String getShortName() {
		return "MultiParse";
	}

}
