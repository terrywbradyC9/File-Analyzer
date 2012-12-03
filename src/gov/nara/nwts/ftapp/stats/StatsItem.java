package gov.nara.nwts.ftapp.stats;

public class StatsItem {
	Class<?> myclass;
	String header;
	int width;
	Object[] values;
	boolean export = true;
	Object initVal;
	
	public StatsItem(Class<?> myclass, String header, int width, Object[] values, boolean export, Object initVal) {
		this.myclass = myclass;
		this.header = header;
		this.width = width;
		this.values = values;
		this.export = export;
		this.initVal = initVal;
	}
	
	public static StatsItem makeStatsItem(Class<?> myclass, String header, int width) {
		return new StatsItem(myclass, header, width, null, false, null);
	}
	
	public static StatsItem makeStringStatsItem(String header, int width) {
		return new StatsItem(String.class, header, width, null, false, "");
	}
	
	public static StatsItem makeStringStatsItem(String header) {
		return new StatsItem(String.class, header, 100, null, false, "");
	}
	
	public static StatsItem makeLongStatsItem(String header) {
		return new StatsItem(Long.class, header, 100, null, false, (long)0);
	}

	public StatsItem setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public StatsItem setExport(boolean export) {
		this.export = export;
		return this;
	}
	
	public StatsItem setValues(Object[] values) {
		this.values = values;
		return this;
	}

	public StatsItem setClass(Class<?> myclass) {
		this.myclass = myclass;
		return this;
	}
	
	public StatsItem setHeader(String header) {
		this.header = header;
		return this;
	}
	
	public StatsItem setInitVal(Object initVal) {
		this.initVal = initVal;
		return this;
	}

	
	public static <T extends Enum<T>> StatsItem makeEnumStatsItem(Class<T> eclass, String name) {
		return new StatsItem(eclass, name, 100, eclass.getEnumConstants(), true, eclass.getEnumConstants()[0]);
	}

	public static <T extends Enum<T>> StatsItem makeEnumStatsItem(Class<T> eclass) {
		return makeEnumStatsItem(eclass, eclass.getName());
	}

	public Object[] array() {
		Object[] obj = new Object[5];
		obj[0] = myclass;
		obj[1] = header;
		obj[2] = width;
		obj[3] = values;
		obj[4] = export;
		return obj;
	}

	public static <T extends Enum<T> & StatsItemEnum> Object[][] toObjectArray(Class<T> eclass) {
		Object[][] obj = new Object[eclass.getEnumConstants().length][];
		int count = 0;
		for(StatsItemEnum curE : eclass.getEnumConstants()) {
			obj[count] = curE.si().array();
			count++;
		}
		return obj;
	}
}
