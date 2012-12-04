package gov.nara.nwts.ftapp.stats;

import java.util.ArrayList;

public class StatsItemConfig extends ArrayList<StatsItem> {

	private static final long serialVersionUID = 366593624998931283L;
	
	public static <T extends Enum<T> & StatsItemEnum> StatsItemConfig create(Class<T> eclass){
		return new StatsItemConfig(eclass);
	}

	public <T extends Enum<T> & StatsItemEnum> StatsItemConfig(Class<T> eclass) {
		for(StatsItemEnum eitem : eclass.getEnumConstants()) {
			add(eitem.si());
		}
	}
	
	public StatsItemConfig() {
	}

	public void addStatsItem(StatsItem si) {
		add(si);
	}
	
	
	public boolean[] getExportArray() {
		boolean[] exarr = new boolean[size()];
		int count = 0;
		for(StatsItem si: this) {
			exarr[count++] = si.export;
		}
		return exarr;
	}
}
