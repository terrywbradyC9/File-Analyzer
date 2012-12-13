package edu.georgetown.library.fileAnalyzer.dateValidation;

import gov.nara.nwts.ftapp.YN;

import java.util.Date;

public class DateValidationResult {
	public Date date;
	public String result;
	public DateValidationStatus status;
	
	public DateValidationResult(Date date, String result, DateValidationStatus status) {
		this.date = date;
		this.result = result;
		this.status = status;
	}
	
	public boolean doNext() {
		return (this.status == DateValidationStatus.UNTESTABLE);
	}
	
	public static DateValidationResult untestable() {
		return new DateValidationResult(null, "", DateValidationStatus.UNTESTABLE);
	}

	public static DateValidationResult missing() {
		return new DateValidationResult(null, "", DateValidationStatus.MISSING);
	}
	public static DateValidationResult invalid() {
		return new DateValidationResult(null, "", DateValidationStatus.INVALID);
	}
	public static DateValidationResult valid(Date d, String s) {
		return new DateValidationResult(d, s, DateValidationStatus.VALID);
	}
	public static DateValidationResult simplifiable(Date d, String s) {
		return new DateValidationResult(d, s, DateValidationStatus.SIMPLIFIABLE);
	}
	public static DateValidationResult parseable(Date d, String s) {
		return new DateValidationResult(d, s, DateValidationStatus.PARSEABLE);
	}
	public static DateValidationResult parseable(String s) {
		return new DateValidationResult(null, s, DateValidationStatus.PARSEABLE);
	}
	public static DateValidationResult valid(String s) {
		return new DateValidationResult(null, s, DateValidationStatus.VALID);
	}
	public YN exists() {
		return (status.exists()) ? YN.Y : YN.N;
	}
}
