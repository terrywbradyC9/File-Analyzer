package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.CounterValidation;

import java.util.Vector;

/**
 * Activates the Importers that will be presented on the Import tab.
 * @author TBrady
 *
 */
public class ImporterRegistry extends Vector<Importer> {
	
	private static final long serialVersionUID = 1L;

	public ImporterRegistry(FTDriver dt) {
		add(new DelimitedFileImporter(dt));
		add(new Parser(dt));
		add(new CountKey(dt));
		add(new CounterValidation(dt));
	}
	
	public void removeImporter(Class<?> c) {
		for(Importer ft: this) {
			if (c.isInstance(ft)) {
				this.remove(ft);
				break;
			}
		}		
	}

}
