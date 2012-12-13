package edu.georgetown.library.fileAnalyzer.dateValidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class DateValidationPattern {
	Pattern criteria;
	
	public DateValidationPattern(String criteriaStr) {
		criteria = Pattern.compile(criteriaStr);
	}

	public DateValidationResult test(String s) {
		if (s == null) return DateValidationResult.untestable();
		s = s.trim();
		if (s.equals("")) return DateValidationResult.missing();
		Matcher m = criteria.matcher(s);
		if (!m.matches()) return DateValidationResult.untestable();
		return makeResult(s, m);
	}
	
	abstract public DateValidationResult makeResult(String s, Matcher m);
}
