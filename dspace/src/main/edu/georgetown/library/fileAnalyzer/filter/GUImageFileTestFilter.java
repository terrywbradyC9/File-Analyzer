package edu.georgetown.library.fileAnalyzer.filter;

import gov.nara.nwts.ftapp.filter.DefaultFileTestFilter;

/**
 * Filter for TIF or JPG files
 * @author TBrady
 *
 */
public class GUImageFileTestFilter extends DefaultFileTestFilter {

	public String getSuffix() {
		return ".*\\.(tif|jpg|jp2)$";
	}
	public boolean isReSuffix() {
		return true;
	}
    public String getName(){return "Image";}

}
