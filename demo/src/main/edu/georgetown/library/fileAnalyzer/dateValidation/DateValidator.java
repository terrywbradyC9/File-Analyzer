package edu.georgetown.library.fileAnalyzer.dateValidation;

import java.util.Vector;

public class DateValidator {
	Vector<DateValidationPattern> patterns;
	String defaultResultFormatStr;
	
	public DateValidator(String defaultResultFormatStr) {
		patterns = new Vector<DateValidationPattern>();
		this.defaultResultFormatStr = defaultResultFormatStr;
	}

	public void addValidationPattern(String pattern, String dateFormatString) {
		addValidationPattern(pattern, dateFormatString, defaultResultFormatStr);
	}

	public void addValidationPattern(DateValidationPattern dvp) {
		patterns.add(dvp);
	}
	
	public void addValidationPattern(String pattern, String dateFormatString, String resultFormatStr) {
		patterns.add(new DefaultDateValidationPattern(pattern, dateFormatString, resultFormatStr));
	}  
	
	public DateValidationResult test(String s) {
		for(DateValidationPattern dvp: patterns) {
			DateValidationResult dvr = dvp.test(s);
			if (!dvr.doNext()) {
				return dvr;
			}
		}
		return DateValidationResult.invalid();
	}
	
}
