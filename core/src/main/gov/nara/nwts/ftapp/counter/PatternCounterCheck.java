package gov.nara.nwts.ftapp.counter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternCounterCheck extends CounterCheck {
	Pattern patt;
	Pattern fixmatch;
	String rep;
	public PatternCounterCheck(Pattern patt) {
		this.patt = patt;
	}
	public PatternCounterCheck(Pattern patt, Pattern fixmatch, String rep) {
		this.patt = patt;
		this.rep = rep;
		this.fixmatch = fixmatch;
	}
	@Override
	public CheckResult performCheck(CounterData cd, Cell cell, String cellval) {
		if (patt == null || cellval == null) {
			return CheckResult.createCellInvalid(cell, message);			
		}
		Matcher m = patt.matcher(cellval);
		if (m.matches()) {
			return CheckResult.createCellValid(cell);
		}
		
		if (fixmatch != null && rep != null) {
			m = fixmatch.matcher(cellval);
			String newVal = m.replaceAll(rep);
			m = patt.matcher(newVal);
			if (m.matches()) {
				return CheckResult.createCellStatus(cell, CounterStat.WARNING).setMessage(message).setNewVal(newVal);
			} else {
				return CheckResult.createCellInvalid(cell, message);
			}
		}

		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message);
		return res;
	}

}
