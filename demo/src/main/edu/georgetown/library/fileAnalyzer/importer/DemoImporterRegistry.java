package edu.georgetown.library.fileAnalyzer.importer;

import edu.georgetown.library.fileAnalyzer.importer.demo.CreateDateParser;
import edu.georgetown.library.fileAnalyzer.importer.demo.MarcImporter;
import edu.georgetown.library.fileAnalyzer.importer.demo.TabSepToDC;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;


/**
 * Activates the Importers that will be presented on the Import tab.
 * @author TBrady
 *
 */
public class DemoImporterRegistry extends DSpaceImporterRegistry {
	
	private static final long serialVersionUID = 1L;

	public DemoImporterRegistry(FTDriver dt) {
		super(dt);
	}
	

}
