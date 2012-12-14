package edu.georgetown.library.fileAnalyzer;

import java.io.File;

import edu.georgetown.library.fileAnalyzer.filetest.DSpaceActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.DSpaceImporterRegistry;

import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.gui.DirectoryTable;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class DSpaceFileAnalyzer extends DirectoryTable {

	public DSpaceFileAnalyzer(File f, boolean modifyAllowed) {
		super(f, modifyAllowed);
	}
	
	@Override protected ActionRegistry getActionRegistry() {
		return new DSpaceActionRegistry(this, true);
	}

	@Override protected ImporterRegistry getImporterRegistry() {
		return new DSpaceImporterRegistry(this);
	}
	public static void main(String[] args) {
		if (args.length > 0)
			new DSpaceFileAnalyzer(new File(args[0]), false);		
		else
			new DSpaceFileAnalyzer(null, false);		
	}

}
