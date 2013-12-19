package gov.nara.nwts.ftapp.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

public class StatsItemConfig implements Iterable<StatsItem>  {
	ArrayList<StatsItem> columns;
	HashMap<Object, StatsItem> columnMap;
	
	public static <T extends Enum<T> & StatsItemEnum> StatsItemConfig create(Class<T> eclass){
		return new StatsItemConfig(eclass);
	}

	public <T extends Enum<T> & StatsItemEnum> StatsItemConfig(Class<T> eclass) {
		this();
		for(StatsItemEnum eitem : eclass.getEnumConstants()) {
			addStatsItem(eitem, eitem.si());
		}
	}
	
	public StatsItemConfig() {
		columns = new ArrayList<StatsItem>();
		columnMap = new HashMap<Object, StatsItem>();
	}
	
	public void addStatsItem(Object key, StatsItem si) {
		si.setIndex(columns.size()-1);
		columns.add(si);
		columnMap.put(key, si);
	}
	
	public int size() {return columns.size();}
	
	public StatsItem get(int i) {
		return columns.get(i);
	}
	
	public StatsItem getByKey(Object key) {
		return columnMap.get(key);
	}
	
	public boolean[] getExportArray() {
		boolean[] exarr = new boolean[size()];
		int count = 0;
		for(StatsItem si: columns) {
			exarr[count++] = si.export;
		}
		return exarr;
	}

	public Iterator<StatsItem> iterator() {
		return columns.iterator();
	}

	public static Object[] getUniqueVals(TreeMap<String,Stats> data, StatsItemEnum si) {
		HashSet<Object> vals = new HashSet<Object>();
		for(Stats stat: data.values()) {
			Object obj = stat.getVal(si);
			if (obj == null) continue;
			vals.add(obj);
		}
		return vals.toArray();
	}
	
	public void setUniqueVals(TreeMap<String,Stats> data, StatsItemEnum si) {
		if (this.size() > si.ordinal()) {
			this.get(si.ordinal()).values = StatsItemConfig.getUniqueVals(data, si);			
		}
	}
	
}
