package edu.georgetown.library.fileAnalyzer;

import edu.georgetown.library.fileAnalyzer.filetest.DSpaceActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.DSpaceImporterRegistry;

import gov.nara.nwts.ftapp.BatchAnalyzer;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class DSpaceBatchAnalyzer extends BatchAnalyzer {

	public DSpaceBatchAnalyzer() {
		super();
	}
	
	public ActionRegistry getActionRegistry(FTDriver ft) {
		return new DSpaceActionRegistry(ft, true);
	}

	protected ImporterRegistry getImporterRegistry(FTDriver ft) {
		return new DSpaceImporterRegistry(ft);
	}
	public static void main(String[] args) {
		DSpaceBatchAnalyzer ba = new DSpaceBatchAnalyzer();
		ba.run(args);
		
	}

}
