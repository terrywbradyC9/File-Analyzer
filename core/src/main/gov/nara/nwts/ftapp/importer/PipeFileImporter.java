package gov.nara.nwts.ftapp.importer;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.importer.DelimitedFileImporter;

/**
 * Importer for semicolon delimited files
 * @author TBrady
 *
 */
public class PipeFileImporter extends DelimitedFileImporter {

	public PipeFileImporter(FTDriver dt) {
		super(dt);
	}

	public String getSeparator() {
		return "|";
	}

	public String toString() {
		return "Import Pipe-Separated File";
	}
	public String getDescription() {
		return "This rule will import a pipe separated file and use the first column as a unique key";
	}
	public String getShortName() {
		return "PIPE";
	}

}
