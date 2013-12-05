package gov.nara.nwts.ftapp.counter;

public enum REV {
	NA,R1,R3,R4;
	
	public static REV find(String s) {
		for(REV rev: REV.values()) {
			if (rev.name().equals(s)) return rev;
		}
		return REV.NA;
	}
}
