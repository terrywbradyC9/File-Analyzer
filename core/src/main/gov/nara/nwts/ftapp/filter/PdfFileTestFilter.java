package gov.nara.nwts.ftapp.filter;

import gov.nara.nwts.ftapp.filter.DefaultFileTestFilter;

/**
 * Filter for TIF or JPG files
 * @author TBrady
 *
 */
public class PdfFileTestFilter extends DefaultFileTestFilter {

	public String getSuffix() {
		return ".*\\.(pdf)$";
	}
	public boolean isReSuffix() {
		return true;
	}
    public String getName(){return "PDF";}

}
