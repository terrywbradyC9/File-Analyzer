package edu.georgetown.library.fileAnalyzer.dateValidation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

public class DefaultDateValidationPattern extends DateValidationPattern {
	DateFormat parseFormat;
	DateFormat returnFormat;
	
	public DefaultDateValidationPattern(String criteriaStr, String parseFormatStr, String returnFormatStr) {
		super(criteriaStr);
		parseFormat = new SimpleDateFormat(parseFormatStr);
		parseFormat.setLenient(false);
		returnFormat = new SimpleDateFormat(returnFormatStr);
	}

	public DateValidationResult makeResult(String s, Matcher m) {
		try {
			Date d = parseFormat.parse(s);
			String ret = returnFormat.format(d);
			Date d2 = returnFormat.parse(ret);
			if (!d.equals(d2)) {
				return DateValidationResult.simplifiable(d, ret);				
			}
			if (!s.equals(ret)){
				return DateValidationResult.parseable(d, ret);				
			}
			return DateValidationResult.valid(d, ret);
		} catch (ParseException e) {
			return DateValidationResult.invalid();
		}
		
	}
}
