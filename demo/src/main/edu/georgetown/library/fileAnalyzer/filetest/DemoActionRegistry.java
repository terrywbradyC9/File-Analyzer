package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.CounterValidation;
import gov.nara.nwts.ftapp.filetest.FileTest;
/** 
 * Initialize the File Analzyer with generic image processing rules (but not NARA specific business rules)
 * @author TBrady
 *
 */
public class DemoActionRegistry extends DSpaceActionRegistry {
	
	private static final long serialVersionUID = 1L;

	public DemoActionRegistry(FTDriver dt, boolean modifyAllowed) {
		super(dt, modifyAllowed);
		add(new PageCount(dt));
		add(new ImageProperties(dt));
		add(new YearbookNameValidationTest(dt)); 
		add(new CreateBag(dt)); 
		add(new VerifyBag(dt)); 
		remove(CounterValidation.class);
		add(new CounterValidationXls(dt)); 
	}
	
	public void remove(Class<?> c) {
		for(FileTest ft: this) {
			if (ft.getClass().isInstance(c)) {
				this.remove(ft);
				break;
			}
		}		
	}
}
