package gov.nara.nwts.ftapp.filter;

import gov.nara.nwts.ftapp.filter.DefaultFileTestFilter;

/**
 * Filter for TIF or JPG files
 * @author TBrady
 *
 */
public class Jp2FileTestFilter extends DefaultFileTestFilter {

	public String getSuffix() {
		return ".*\\.(jp2)$";
	}
	public boolean isReSuffix() {
		return true;
	}
    public String getName(){return "JP2";}

}
