package gov.nara.nwts.ftapp.filter;

/**
 * Filter for excel files
 * @author TBrady
 *
 */
public class CounterFilter extends DefaultFileTestFilter {
	public String getSuffix() {
		return ".*\\.(xls|xlsx|csv|txt)$";
	}
	public boolean isReSuffix() {
		return true;
	}
    public String getName(){return "Counter";}

}
