package gov.nara.nwts.ftapp.filter;

/**
 * Filter for XML files
 * @author TBrady
 *
 */
public class ZipFilter extends DefaultFileTestFilter {
	public String getSuffix() { 
		return ".zip";
	}
    public String getName(){return "ZIP";}

}
