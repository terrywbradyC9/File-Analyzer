package gov.nara.nwts.ftapp.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.YN;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract class for rules that report on checksum values; derived versions of this class will provide a specific hashing algorithm from the Java core library.
 * @author TBrady
 *
 */
public abstract class NameChecksum extends DefaultFileTest {
	
	public static enum ChecksumStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Key", 400)),
		Data(StatsItem.makeStatsItem(Object.class, "Data", 300).setInitVal("")),
		Duplicate(StatsItem.makeEnumStatsItem(YN.class, "Duplicate").setInitVal(YN.N)),
		MatchCount(StatsItem.makeIntStatsItem("Num of Matches").setInitVal(1));
		
		StatsItem si;
		ChecksumStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public class ChecksumStats extends Stats {
		
		public ChecksumStats(String key) {
			super(key);
			init(ChecksumStatsItems.class);
		}
		
		public Object compute(File f, FileTest fileTest) {
			Object o = fileTest.fileTest(f);
			setVal(ChecksumStatsItems.Data, o);
			
			if (fileTest instanceof NameChecksum) {
				if (o != null) {
					((NameChecksum)fileTest).setChecksumKey(o.toString(), this);				
				}
			}
			return o;
		}
	}
	
	HashMap<String, List<ChecksumStats>> keymap;

	public NameChecksum(FTDriver dt) {
		super(dt);
		keymap = new HashMap<String, List<ChecksumStats>>();
	}

	public String toString() {
		return "Sort By Checksum";
	}
	public String getKey(File f) {
		return getRelPath(f);
	}
	
	@Override public void init() {
		keymap.clear();
	}
	
	public void setChecksumKey(String s, ChecksumStats stat) {
		List<ChecksumStats> matches = keymap.get(s);
		if (matches == null) {
			matches = new ArrayList<ChecksumStats>();
			keymap.put(s, matches);
		} 
		matches.add(stat);
	}
	
	@Override public void refineResults() {
		for(List<ChecksumStats> matches: keymap.values()) {
			if (matches.size() == 1) continue;
			for(ChecksumStats match: matches) {
				match.setVal(ChecksumStatsItems.Duplicate, YN.Y);
				match.setVal(ChecksumStatsItems.MatchCount, matches.size());
			}
		}
	}
	
    public String getShortName(){return "Checksum";}

    abstract public MessageDigest getMessageDigest() throws NoSuchAlgorithmException;
    
    public String getChecksum(File f) {
    	FileInputStream fis = null;
		try {
			MessageDigest md = getMessageDigest();
			fis = new FileInputStream(f);
			byte[] dataBytes = new byte[1204];
			int nread = 0;
			while((nread = fis.read(dataBytes)) != -1){
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<mdbytes.length; i++){
				sb.append(Integer.toString((mdbytes[i] & 0xFF) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis!=null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;    	
    }
    
	public Object fileTest(File f) {
		return getChecksum(f);
	}
    public Stats createStats(String key){ 
    	return new ChecksumStats(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return StatsItemConfig.create(ChecksumStatsItems.class);

    }
	public void initFilters() {
		initAllFilters();
	}

	public String getDescription() {
		return "This test reports the checksum for a given filename.\nNote, the checksum will be overwritten if the file is found more than once.";
	}
    public void progress(int count) {
    	if (count % 5000 == 0) {
    		cleanup(count);
    	} else if (count % 100 == 0){
    		showCount(count);
    	}
    }

}
