package edu.georgetown.library.fileAnalyzer;

import edu.georgetown.library.fileAnalyzer.filetest.GUActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.DemoImporterRegistry;

import gov.nara.nwts.ftapp.BatchAnalyzer;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class DemoBatchAnalyzer extends BatchAnalyzer {

	public DemoBatchAnalyzer() {
		super();
	}
	
	public ActionRegistry getActionRegistry(FTDriver ft) {
		return new GUActionRegistry(ft, true);
	}

	protected ImporterRegistry getImporterRegistry(FTDriver ft) {
		return new DemoImporterRegistry(ft);
	}
	public static void main(String[] args) {
		DemoBatchAnalyzer ba = new DemoBatchAnalyzer();
		ba.run(args);
	}

}
