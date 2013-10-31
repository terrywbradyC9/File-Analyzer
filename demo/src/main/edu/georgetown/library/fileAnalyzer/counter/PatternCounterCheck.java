package edu.georgetown.library.fileAnalyzer.counter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternCounterCheck extends CounterCheck {
	Pattern patt;
	Pattern fixmatch;
	String rep;
	PatternCounterCheck(Pattern patt) {
		this.patt = patt;
	}
	PatternCounterCheck(Pattern patt, Pattern fixmatch, String rep) {
		this.patt = patt;
		this.rep = rep;
		this.fixmatch = fixmatch;
	}
	@Override
	CheckResult performCheck(Cell cell, String cellval) {
		Matcher m = patt.matcher(cellval);
		if (m.matches()) {
			return CheckResult.createCellValid(cell);
		}
		
		if (fixmatch != null && rep != null) {
			m = fixmatch.matcher(cellval);
			String newVal = m.replaceAll(rep);
			m = patt.matcher(newVal);
			if (m.matches()) {
				return CheckResult.createCellStatus(cell, CounterStat.FIXABLE).setMessage(message).setNewVal(newVal);
			} else {
				return CheckResult.createCellInvalid(cell, message + ": " + cellval);
			}
		}

		CheckResult res = CheckResult.createCellStatus(cell, stat).setMessage(message + ": " + cellval);
		return res;
	}

}
