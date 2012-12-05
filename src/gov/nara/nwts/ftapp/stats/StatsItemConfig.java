package gov.nara.nwts.ftapp.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
}
