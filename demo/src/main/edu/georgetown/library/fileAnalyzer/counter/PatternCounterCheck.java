package edu.georgetown.library.fileAnalyzer.counter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternCounterCheck extends CounterCheck {
	Pattern patt;
	String rep;
	PatternCounterCheck(Pattern patt) {
		this.patt = patt;
	}
	PatternCounterCheck(Pattern patt, String rep) {
		this.patt = patt;
		this.rep = rep;
	}
	@Override
	CheckResult performCheck(Cell cell, String cellval) {
		Matcher m = patt.matcher(cellval);
		if (m.matches()) {
			return CheckResult.createCellValid(cell);
		}
		
		if (stat == CounterStat.FIXABLE && rep != null) {
			String newVal = m.replaceAll(rep);
			m = patt.matcher(newVal);
			if (m.matches()) {
				return CheckResult.createCellStatus(cell, stat).setMessage(message).setNewVal(newVal);
			} else {
				return CheckResult.createCellInvalid(cell, message);
			}
		}

		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message);
		return res;
	}

}
