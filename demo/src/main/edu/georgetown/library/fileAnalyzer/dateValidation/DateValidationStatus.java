package edu.georgetown.library.fileAnalyzer.dateValidation;

public enum DateValidationStatus {
	VALID(true), SIMPLIFIABLE(true), PARSEABLE(false), INVALID(false), MISSING(false), UNTESTABLE(false);
	
	boolean valid;
	DateValidationStatus(boolean valid){
		this.valid = valid;
	}
	
	public boolean isValid() {
		return valid;
	}

	public static DateValidationStatus overall(DateValidationStatus a, DateValidationStatus b){
		return (a.ordinal() > b.ordinal()) ? a : b;
	}

	public static DateValidationStatus overall(DateValidationStatus[] dstats){
		DateValidationStatus ret = DateValidationStatus.VALID;
		for(DateValidationStatus dvs: dstats) {
			ret = (ret.ordinal() > dvs.ordinal()) ? ret : dvs;
		}
		
		return ret;
	}

	public static DateValidationStatus worst(DateValidationStatus a, DateValidationStatus b){
		return (a.ordinal() > b.ordinal()) ? a : b;
	}

	public static DateValidationStatus best(DateValidationStatus a, DateValidationStatus b){
		return (a.ordinal() < b.ordinal()) ? a : b;
	}

	public boolean exists() {
		if ((this == DateValidationStatus.MISSING) || (this == DateValidationStatus.UNTESTABLE)) return false;
		return true;
	}
	
}
