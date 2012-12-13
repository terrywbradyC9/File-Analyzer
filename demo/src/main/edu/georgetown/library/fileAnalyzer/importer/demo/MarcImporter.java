package edu.georgetown.library.fileAnalyzer.importer.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

/**
 * Importer for tab delimited files
 * @author TBrady
 *
 */
public class MarcImporter extends DefaultImporter {

	private static enum MarcStatsItems implements StatsItemEnum {
		Leader(StatsItem.makeStringStatsItem("Leader").setExport(false)),
		Author(StatsItem.makeStringStatsItem("Author 100", 300)),
		Dates(StatsItem.makeStringStatsItem("Dates", 160)),
		Title(StatsItem.makeStringStatsItem("Title 245", 300)),
		Title_h(StatsItem.makeStringStatsItem("Title $h", 200)),
		Title_b(StatsItem.makeStringStatsItem("Title $b", 200)),
		;
		
		StatsItem si;
		MarcStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new Stats(details, key);}
	}
	static StatsItemConfig details = StatsItemConfig.create(MarcStatsItems.class);
	
	public MarcImporter(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "Demo: Import Key Marc Info";
	}
	public String getDescription() {
		return "Prototype: This rule will perform a simple parse of a MARC record";
	}
	public String getShortName() {
		return "Marc";
	}
	
	class Split {
		String before ="";
		String after ="";
		String otherwise ="";
		Split(String s, String sep) {
			before = s;
			int i = s.indexOf(sep);
			if (i!=-1) {
				before = s.substring(0, i);
				after = s.substring(i + sep.length());
				otherwise = after;
			} else {
				otherwise = s;
			}
		}
	}
	
	public ActionResult importFile(File selectedFile) throws IOException {
		Pattern p = Pattern.compile("^=(LDR|\\d\\d\\d)\\s+....(.*)$");
		Timer timer = new Timer();
		FileReader fr = new FileReader(selectedFile);
		BufferedReader br = new BufferedReader(fr);
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		Stats rec = null;
		for(String line=br.readLine(); line!=null; line=br.readLine()){
			Matcher m = p.matcher(line);
			if (m.matches()) {
				String field = m.group(1);
				String val = m.group(2);
				
				if (field.equals("LDR")) {
					if (rec != null) {
						types.put(rec.key, rec);
					}
					rec = Generator.INSTANCE.create(val);
				} else if (rec == null) {
					continue;
				} else if (field.equals("100")) {
					Split f100 = new Split(val, "$d");
					rec.setVal(MarcStatsItems.Author, f100.before);
					rec.setVal(MarcStatsItems.Dates, f100.after);
				} else if (field.equals("245")) {
					Split f245 = new Split(val, "$h");
					rec.setVal(MarcStatsItems.Title,f245.before);
					f245 = new Split(f245.otherwise, "$b");
					rec.setVal(MarcStatsItems.Title_h, f245.before);
					rec.setVal(MarcStatsItems.Title_b, f245.after);
				}
			}
		}
		fr.close();
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), details, types, true, timer.getDuration());
	}
}
