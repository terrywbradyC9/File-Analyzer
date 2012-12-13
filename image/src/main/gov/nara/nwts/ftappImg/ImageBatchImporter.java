package gov.nara.nwts.ftappImg;

import gov.nara.nwts.ftapp.BatchImporter;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
import gov.nara.nwts.ftappImg.filetest.ImageActionRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class ImageBatchImporter extends BatchImporter {

	public ImageBatchImporter() {
		super();
	}
	
	public ActionRegistry getActionRegistry(FTDriver ft) {
		return new ImageActionRegistry(ft, true);
	}

	public ImporterRegistry getImporterRegistry(FTDriver ft) {
		return new ImporterRegistry(ft);
	}
	public static void main(String[] args) {
		ImageBatchImporter ba = new ImageBatchImporter();
		ba.run(args);
	}

}
