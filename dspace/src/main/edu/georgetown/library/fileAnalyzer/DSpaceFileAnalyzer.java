package edu.georgetown.library.fileAnalyzer;

import java.io.File;

import edu.georgetown.library.fileAnalyzer.filetest.GUActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.GUImporterRegistry;

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
	
	protected ActionRegistry getActionRegistry() {
		return new GUActionRegistry(this, modifyAllowed);
	}

	protected ImporterRegistry getImporterRegistry() {
		return new GUImporterRegistry(this);
	}
	public static void main(String[] args) {
		if (args.length > 0)
			new DSpaceFileAnalyzer(new File(args[0]), false);		
		else
			new DSpaceFileAnalyzer(null, false);		
	}

}
