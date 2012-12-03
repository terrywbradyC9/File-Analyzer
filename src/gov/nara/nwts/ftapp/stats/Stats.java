package gov.nara.nwts.ftapp.stats;

import java.io.File;
import java.util.Vector;

import gov.nara.nwts.ftapp.filetest.FileTest;

/**
 * Base class for Stats objects containing a key and a variable lenght list of values.
 * @author TBrady
 *
 */
public class Stats {
	public static enum StatsItems implements StatsItemEnum {
		Type(StatsItem.makeStringStatsItem("Type"));
		
		StatsItem si;
		StatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static Object[][] details = StatsItem.toObjectArray(StatsItems.class);
	
	public StatsItem header;
	private Vector<Object> vals;
	public String key;
	
	public Stats(String key) {
		this.key = key;
		vals = new Vector<Object>();
	}
	 
	public void setVal(StatsItemEnum eitem, Object val) {
		int index = eitem.ordinal() - 1; 
		if (vals.size() > index) {
			vals.set(index,val);
		}
	}
	 
	public Vector<Object> getVals() {
		return vals;
	}
	
	
	public void addExtraVal(Object val) {
		vals.add(val);
	}
	
	public Object getExtraVal(int base, int index) {
		return vals.get(base+index);
	}
	
	public Object getVal(StatsItemEnum eitem) {
		return getVal(eitem, null);
	}
	
	public Long getLongVal(StatsItemEnum eitem) {
		return (Long)getVal(eitem, null);
	}

	public Object getVal(StatsItemEnum eitem, Object def) {
		int index = eitem.ordinal() - 1;
		if (vals.size() > index) {
			return vals.get(index);
		}
		return def;
	}
	 
	public <T extends Enum<T> & StatsItemEnum> void init(Class<T> eclass) {
		boolean first = true;
		vals.clear();
		for(StatsItemEnum item: eclass.getEnumConstants()) {
			if (first) {
				first = false;
			} else {
				vals.add(item.si().initVal);
			}
		}
	}
	
	public Object compute(File f, FileTest fileTest) {
		Object o = fileTest.fileTest(f);
		return o;
	}
}
