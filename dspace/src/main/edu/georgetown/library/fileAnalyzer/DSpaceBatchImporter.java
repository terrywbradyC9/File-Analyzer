package edu.georgetown.library.fileAnalyzer;

import edu.georgetown.library.fileAnalyzer.filetest.GUActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.GUImporterRegistry;

import gov.nara.nwts.ftapp.BatchImporter;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class DSpaceBatchImporter extends BatchImporter {

	public DSpaceBatchImporter() {
		super();
	}
	
	public ActionRegistry getActionRegistry(FTDriver ft) {
		return new GUActionRegistry(ft, true);
	}

	public ImporterRegistry getImporterRegistry(FTDriver ft) {
		return new GUImporterRegistry(ft);
	}
	public static void main(String[] args) {
		DSpaceBatchImporter ba = new DSpaceBatchImporter();
		ba.run(args);
	}

}
