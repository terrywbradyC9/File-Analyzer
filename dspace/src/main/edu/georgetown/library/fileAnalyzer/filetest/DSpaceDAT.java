package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filter.DefaultFileTestFilter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
public class DSpaceDAT extends DefaultFileTest { 
	
	public static enum DSpaceDATStatsItems implements StatsItemEnum {
		FileName(StatsItem.makeStringStatsItem("File name", 220)),
		Start(StatsItem.makeStringStatsItem("Start", 100)),
		End(StatsItem.makeStringStatsItem("End", 100)),
		Submit(StatsItem.makeIntStatsItem("Submissions")),
		AllItems(StatsItem.makeIntStatsItem("All Items")),
		Search(StatsItem.makeIntStatsItem("Search")),
		Browse(StatsItem.makeIntStatsItem("Browse")),
		ViewHome(StatsItem.makeIntStatsItem("View Home")),
		ViewItem(StatsItem.makeIntStatsItem("View Item")),
		AvgViews(StatsItem.makeIntStatsItem("Avg Views")),
		;
		
		StatsItem si;
		DSpaceDATStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {return new Stats(details, key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(DSpaceDATStatsItems.class);
	
	public DSpaceDAT(FTDriver dt) {
		super(dt);
	}
	
	public String toString() {
		return "DSpaceDAT";
	}
	public String getKey(File f) {
		return f.getName();
	}
	
    public String getShortName(){return "DSpace DAT";}

	public Object fileTest(File f) {
		Stats stats = getStats(f);
		Pattern p1 = Pattern.compile("^(start_date|end_date)=(\\d\\d)/(\\d\\d)/(\\d\\d\\d\\d)$");
		Pattern p2 = Pattern.compile("^(archive.All Items|action.search|action.browse|action.submission_complete|action.view_community_list|action.view_item|avg_item_views)=(\\d+)$");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			for(String line = br.readLine(); line!=null; line=br.readLine()) {
				Matcher m=p1.matcher(line);
				if (m.matches()) {
					String lkey = m.group(1);
					String val = m.group(4) + "-" + m.group(3) + "-" +m.group(2);
					
					if (lkey.equals("start_date")) {
						stats.setVal(DSpaceDATStatsItems.Start, val);
					} else if (lkey.equals("end_date")) {
						stats.setVal(DSpaceDATStatsItems.End, val);
					}
				}
				m=p2.matcher(line);
				if (m.matches()) {
					String lkey = m.group(1);
					int val = Integer.parseInt(m.group(2));
					
					if (lkey.equals("archive.All Items")) {
						stats.setVal(DSpaceDATStatsItems.AllItems, val);
					} else if (lkey.equals("action.search")) {
						stats.setVal(DSpaceDATStatsItems.Search, val);
					} else if (lkey.equals("action.browse")) {
						stats.setVal(DSpaceDATStatsItems.Browse, val);
					} else if (lkey.equals("action.submission_complete")) {
						stats.setVal(DSpaceDATStatsItems.Submit, val);
					} else if (lkey.equals("action.view_community_list")) {
						stats.setVal(DSpaceDATStatsItems.ViewHome, val);
					} else if (lkey.equals("action.view_item")) {
						stats.setVal(DSpaceDATStatsItems.ViewItem, val);
					} else if (lkey.equals("avg_item_views")) {
						stats.setVal(DSpaceDATStatsItems.AvgViews, val);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

    public Stats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details;
    }

    public String getDescription() {
		return "Analyzes DSpace DAT stats files";
	}
    
    public class DATFilter extends DefaultFileTestFilter {
    	public String getSuffix() {
    		return ".dat";
    	}
    	public String getPrefix() {
    		return "dspace-log-monthly";
    	}
    	
    }
	public void initFilters() {
		filters.add(new DATFilter());
	}

}
