package gov.nara.nwts.ftapp.counter;

import java.util.HashMap;

public class ListCounterCheck extends CounterCheck {
	HashMap<String,String> valueMap;
	public ListCounterCheck(String[] vals) {
		message = "Cell label does not contain expected value";
		this.valueMap = new HashMap<String,String>();
		for(String s: vals) {
			this.valueMap.put(s, s);
		}
	}
	
	public void addAlternative(String s, String rep) {
		valueMap.put(s, rep);
	}
	
	@Override
	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (cellval == null) cellval = "";
		String mapv = valueMap.get(cellval);
		if (mapv == null) {
			mapv = valueMap.get(cellval.trim());
		}
		if (mapv == null) {
			return CheckResult.createCellStatus(cell, stat).setMessage(message);			
		}
		if (!mapv.equals(cellval)) {
			if (mapv.equals(cellval.trim())) {
				return CheckResult.createCellStatus(cell, stat).setMessage("Cell value contains extra white space").setNewVal(mapv);										
			}
			return CheckResult.createCellStatus(cell, stat).setMessage(message).setNewVal(mapv);						
		}
		
		return CheckResult.createCellValid(cell);
	}

}
