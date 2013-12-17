package gov.nara.nwts.ftapp.filter;

/**
 * Filter for text files
 * @author TBrady
 *
 */
public class TsvFilter extends DefaultFileTestFilter {
	public String getSuffix() { 
		return ".tsv";
	}
    public String getName(){return "TSV";}

}
