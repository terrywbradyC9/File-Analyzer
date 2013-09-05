package edu.georgetown.library.fileAnalyzer.importer;

import gov.nara.nwts.ftapp.FTDriver;


/**
 * Activates the Importers that will be presented on the Import tab.
 * @author TBrady
 *
 */
public class DemoImporterRegistry extends DSpaceImporterRegistry {
	
	private static final long serialVersionUID = 1L;

	public DemoImporterRegistry(FTDriver dt) {
		super(dt);
		add(new MarcValidator(dt));
	}
	

}
