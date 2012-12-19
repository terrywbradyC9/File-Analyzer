package edu.georgetown.library.fileAnalyzer.importer.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;

import edu.georgetown.library.fileAnalyzer.dateValidation.DateValidationPattern;
import edu.georgetown.library.fileAnalyzer.dateValidation.DateValidationResult;
import edu.georgetown.library.fileAnalyzer.dateValidation.DateValidationStatus;
import edu.georgetown.library.fileAnalyzer.dateValidation.DateValidator;

import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.YN;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.importer.DelimitedFileImporter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

public class CreateDateParser extends DefaultImporter {
	public static String months = "January|Jan|February|Feb|March|Mar|April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|October|Oct|November|Nov|December|Dec";
	
	DateValidator dv;

	public CreateDateParser(FTDriver dt) {
		super(dt);
		dv = new DateValidator("yyyy-MM-dd");
		dv.addValidationPattern("^\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ$", "yyyy-MM-dd'T'HH:mm:ss'Z'");
		dv.addValidationPattern("^\\d\\d\\d\\d-\\d\\d?-\\d\\d?$", "yyyy-MM-dd");
		dv.addValidationPattern("^\\d\\d\\d\\d-\\d\\d?$", "yyyy-MM", "yyyy-MM");
		dv.addValidationPattern("^("+months+") \\d\\d?, \\d\\d\\d\\d$", "MMM dd, yyyy");
		dv.addValidationPattern("^("+months+") \\d\\d\\d\\d$", "MMM yyyy", "yyyy-MM");
		dv.addValidationPattern("^\\d\\d?/\\d\\d?/\\d\\d\\d\\d$", "MM/dd/yyyy");
		dv.addValidationPattern(
				new DateValidationPattern("^0000(0000)?$"){
					public DateValidationResult makeResult(String s, Matcher m) {
						return DateValidationResult.invalid();
					}				
				}
			);
		dv.addValidationPattern(
				new DateValidationPattern("^(\\d\\d\\d\\d)$"){
					public DateValidationResult makeResult(String s, Matcher m) {
						return DateValidationResult.valid(m.group(1));
					}				
				}
			);
		dv.addValidationPattern(
			new DateValidationPattern("^(\\d\\d\\d\\d)0000$"){
				public DateValidationResult makeResult(String s, Matcher m) {
					return DateValidationResult.parseable(m.group(1));
				}				
			}
		);
		dv.addValidationPattern(
				new DateValidationPattern("^(\\d\\d\\d\\d)(\\d\\d)00$"){
					public DateValidationResult makeResult(String s, Matcher m) {
						return DateValidationResult.parseable(m.group(1)+"-"+m.group(2));
					}				
				}
			);
		dv.addValidationPattern("^\\d\\d\\d\\d\\d\\d\\d\\d$", "yyyyMMdd");
	}

    private enum TY {C,I,D,A,V}
	private static List<EnumSet<TY>> getDateCombos(){
		ArrayList<EnumSet<TY>> list = new ArrayList<EnumSet<TY>>();
		for(int ci=0; ci<2; ci++) {
			for(int ii=0; ii<2; ii++) {
				for(int di=0; di<2; di++) {
					for(int ai=0; ai<2; ai++) {
						for(int vi=0; vi<2; vi++) {
							ArrayList<TY> ilist = new ArrayList<TY>();
							if (ci == 0) ilist.add(TY.C);
							if (ii == 0) ilist.add(TY.I);
							if (di == 0) ilist.add(TY.D);
							if (ai == 0) ilist.add(TY.A);
							if (vi == 0) ilist.add(TY.V);
							EnumSet<TY> set = (ilist.isEmpty()) ? EnumSet.noneOf(TY.class) : EnumSet.copyOf(ilist);
							list.add(set);
						}
					}
				}
			}
		}
		return list;
	}
	private static String getDateMix() {
		return "NA";
	}
	private static String getDateMix(EnumSet<TY> set) {
		if (set == null) return getDateMix(); 
		String s = "";
		for(TY t: TY.values()) {
			if (set.contains(t)) {
				s += t.name();
			} else {
				s += "_";
			}
		}
		return s;
	}
	
	private static String getDateMix(DateValidationStatus[] dstats) {
		ArrayList<TY> list = new ArrayList<TY>();
		for(TY t: TY.values()) {
			if (dstats[t.ordinal()].exists()) list.add(t);
		}
		return getDateMix(EnumSet.copyOf(list));
	}

	private static List<String> getDateMixNames() {
		ArrayList<String> list = new ArrayList<String>();
		for(EnumSet<TY> set: getDateCombos()) {
			list.add(getDateMix(set));
		}
		list.add(getDateMix());
		return list;
	}
	
	private static enum DSpaceDateStatsItems implements StatsItemEnum {
		ItemId(StatsItem.makeStringStatsItem("Item Id", 80)),
		OverallStat(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Pass/Fail",DateValidationStatus.MISSING).setWidth(80)),
		ResolvedDate(StatsItem.makeStringStatsItem("Res Date", 130)),
		Title(StatsItem.makeStringStatsItem("Title", 300)),
		Handle(StatsItem.makeStringStatsItem("Handle", 100)),

		DateMix(StatsItem.makeStringStatsItem("DateMix", 80).setValues(getDateMixNames().toArray()).setInitVal(getDateMix())),

		CreateYN(StatsItem.makeEnumStatsItem(YN.class, "Has Create Date?", YN.N).setWidth(60)),
		IssueYN(StatsItem.makeEnumStatsItem(YN.class, "Has Issue Date?", YN.N).setWidth(60)),
		UnqualDateYN(StatsItem.makeEnumStatsItem(YN.class, "Has Unqualified Date?", YN.N).setWidth(60)),
		AccessnYN(StatsItem.makeEnumStatsItem(YN.class, "Has Accession Date?", YN.N).setWidth(60)),
		AvailYN(StatsItem.makeEnumStatsItem(YN.class, "Has Avail Date?", YN.N).setWidth(60)),

		CreateDate(StatsItem.makeStringStatsItem("Creation Date", 130)),
		CreateDateStatus(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Create Date Status", DateValidationStatus.MISSING)),
		CreateDateNormalized(StatsItem.makeStringStatsItem("Creation Date Normalized", 130)),

		IssueDate(StatsItem.makeStringStatsItem("Issue Date", 130)),
		IssueDateStatus(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Issue Date Status", DateValidationStatus.MISSING)),
		IssueDateNormalized(StatsItem.makeStringStatsItem("Issue Date Normalized", 130)),

		UnqualDate(StatsItem.makeStringStatsItem("Unqualified Date", 130)),
		UnqualDateStatus(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Unqualified Date Status", DateValidationStatus.MISSING)),
		UnqualDateNormalized(StatsItem.makeStringStatsItem("Unqualified Date Normalized", 130)),

		AccessnDate(StatsItem.makeStringStatsItem("Accessn Date", 130)),
		AccessnDateStatus(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Accessn Date Status", DateValidationStatus.MISSING)),
		AccessnDateNormalized(StatsItem.makeStringStatsItem("Accessn Date Normalized", 130)),

		AvailDate(StatsItem.makeStringStatsItem("Avail Date", 130)),
		AvailDateStatus(StatsItem.makeEnumStatsItem(DateValidationStatus.class, "Avail Date Status", DateValidationStatus.MISSING)),
		AvailDateNormalized(StatsItem.makeStringStatsItem("Avail Date Normalized", 130)),

		CopyrightDate(StatsItem.makeStringStatsItem("Copyright Date", 100)),
		SubmissionDate(StatsItem.makeStringStatsItem("Submission Date", 100)),
		;
		
		StatsItem si;
		DSpaceDateStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new Stats(details, key);}
	}

	int cols = 8;
	Object[][]mydetails;
	
	static StatsItemConfig details = StatsItemConfig.create(DSpaceDateStatsItems.class);
	public StatsItemConfig getDetails() {
		return details;
	}

	public String toString() {
		return "Demo: Creation Date Analysis";
	}
	public String getDescription() {
		return "Prototype: This rule will parse a pipe separated file containing several unnormalized DSpace date fields.\n" +
				"Thus rule is for illustrative purposes, it does not have an operational purpose." +
				"Expected input format\n" +
				"-Item id\n" +
				"-Community\n" +
				"-Collection\n" +
				"-Title\n" +
				"-Handle\n" +
				"-Creation date\n" +
				"-Issue date\n" +
				"-Unqualified date\n" +
				"-Accession date\n" +
				"-Available date\n" +
				"-Copyright date\n" +
				"-Submission date\n" +
				"";
	}
	public String getShortName() {
		return "Create";
	}
	public ActionResult importFile(File selectedFile) throws IOException {
		Timer timer = new Timer();
		TreeMap<String,Stats> types = new TreeMap<String,Stats>();
		Vector<Vector<String>> data = DelimitedFileImporter.parseFile(selectedFile, "|", true);
		for (Vector<String> v : data) {
			String item = v.get(0);
			Stats stats = Generator.INSTANCE.create(item);
			
			DateValidationStatus[] dstats = new DateValidationStatus[TY.values().length];
			DateValidationStatus itemstatus = DateValidationStatus.MISSING;
			String resdate = "";
			
			for(int i=0; i<dstats.length; i++) {
				dstats[i] =  DateValidationStatus.MISSING;
			}
			
			if (v.size() > 3) stats.setVal(DSpaceDateStatsItems.Title, v.get(3)); /*Title*/
			if (v.size() > 4) stats.setVal(DSpaceDateStatsItems.Handle, v.get(4).replace("http://hdl.handle.net/", "")); /*Handle*/
			
			if (v.size() > 5) {
				String createdate = v.get(5);
				DateValidationResult dvr = dv.test(createdate);
				dstats[0] = dvr.status;
				itemstatus = dvr.status;
				resdate = dvr.result;
				
				stats.setVal(DSpaceDateStatsItems.CreateDate, createdate);
				stats.setVal(DSpaceDateStatsItems.CreateYN, dvr.exists());
				stats.setVal(DSpaceDateStatsItems.CreateDateStatus, dvr.status);
				stats.setVal(DSpaceDateStatsItems.CreateDateNormalized, dvr.result);
			}
			
			if (v.size() > 6) {
				String issuedate = v.get(6);
				DateValidationResult dvr = dv.test(issuedate);
				dstats[1] = dvr.status;
				
				stats.setVal(DSpaceDateStatsItems.IssueDate, issuedate); 
				stats.setVal(DSpaceDateStatsItems.IssueYN, dvr.exists());
				stats.setVal(DSpaceDateStatsItems.IssueDateStatus, dvr.status);
				stats.setVal(DSpaceDateStatsItems.IssueDateNormalized, dvr.result);
			}
			
			if (v.size() > 7) {
				String date = v.get(7);
				DateValidationResult dvr = dv.test(date);
				dstats[2] = dvr.status;
				if (!itemstatus.exists()) {
					itemstatus = dvr.status;
					resdate = dvr.result;
				}
				
				stats.setVal(DSpaceDateStatsItems.UnqualDate, date); 
				stats.setVal(DSpaceDateStatsItems.UnqualDateYN, dvr.exists());
				stats.setVal(DSpaceDateStatsItems.UnqualDateStatus, dvr.status);
				stats.setVal(DSpaceDateStatsItems.UnqualDateNormalized, dvr.result);
			}

			if (v.size() > 8) {
				String accdate = v.get(8);
				DateValidationResult dvr = dv.test(accdate);
				dstats[3] = dvr.status;
				
				stats.setVal(DSpaceDateStatsItems.AccessnDate, accdate); 
				stats.setVal(DSpaceDateStatsItems.AccessnYN, dvr.exists());
				stats.setVal(DSpaceDateStatsItems.AccessnDateStatus,dvr.status);
				stats.setVal(DSpaceDateStatsItems.AccessnDateNormalized, dvr.result);
			}

			if (v.size() > 9) {
				String avdate = v.get(9);
				DateValidationResult dvr = dv.test(avdate);
				dstats[4] = dvr.status;
				
				stats.setVal(DSpaceDateStatsItems.AvailDate, avdate); 
				stats.setVal(DSpaceDateStatsItems.AvailYN, dvr.exists());
				stats.setVal(DSpaceDateStatsItems.AvailDateStatus, dvr.status);
				stats.setVal(DSpaceDateStatsItems.AvailDateNormalized, dvr.result);
			}

			if (v.size() > 10) stats.setVal(DSpaceDateStatsItems.CopyrightDate, v.get(10));
			if (v.size() > 11) stats.setVal(DSpaceDateStatsItems.SubmissionDate, v.get(11));
			
			if (v.size() != 12) {
				itemstatus = DateValidationStatus.UNTESTABLE;
			} 
			stats.setVal(DSpaceDateStatsItems.DateMix, getDateMix(dstats));
			stats.setVal(DSpaceDateStatsItems.OverallStat, itemstatus);
			stats.setVal(DSpaceDateStatsItems.ResolvedDate, resdate);
			types.put(item, stats);			
		}
		
		return new ActionResult(selectedFile, selectedFile.getName(), this.toString(), getDetails(), types, true, timer.getDuration());
	}
	
}
