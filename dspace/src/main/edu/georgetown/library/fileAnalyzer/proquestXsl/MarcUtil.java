package edu.georgetown.library.fileAnalyzer.proquestXsl;

import gov.nara.nwts.ftapp.ftprop.FTProp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MarcUtil {
	public static final String P_UNIV_NAME = "university-name";
	public static final String P_UNIV_LOC = "university-loc";
	public static final String P_EMBARGO_SCHEMA = "embargo-schema";
	public static final String P_EMBARGO_ELEMENT = "embargo-element";
	public static final String P_EMBARGO_TERMS = "embargo-terms";
	public static final String P_EMBARGO_CUSTOM = "embargo-custom-date";
	public static final String P_EMBARGO_LIFT = "embargo-lift-date";
	
	public static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss.SS");
	
	public static HashMap<String, Object> getXslParm(ArrayList<FTProp> props) {
		HashMap<String,Object> pmap = new HashMap<String,Object>();
		pmap.put("recdate", df.format(new Date()));
		for(FTProp prop: props) {
			String key = prop.getShortName();
			pmap.put(key, prop.getValue());
		}
		return pmap;
	}
	
}
