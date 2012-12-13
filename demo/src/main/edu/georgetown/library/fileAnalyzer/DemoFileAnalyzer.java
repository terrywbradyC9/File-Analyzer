package edu.georgetown.library.fileAnalyzer;

import java.io.File;

import edu.georgetown.library.fileAnalyzer.filetest.DemoActionRegistry;
import edu.georgetown.library.fileAnalyzer.importer.DemoImporterRegistry;

import gov.nara.nwts.ftapp.filetest.ActionRegistry;
import gov.nara.nwts.ftapp.gui.DirectoryTable;
import gov.nara.nwts.ftapp.importer.ImporterRegistry;
/**
 * Driver for the File Analyzer GUI loading image-specific rules but not NARA specific rules.
 * @author TBrady
 *
 */
public class DemoFileAnalyzer extends DirectoryTable {

	public DemoFileAnalyzer(File f, boolean modifyAllowed) {
		super(f, modifyAllowed);
	}
	
	protected ActionRegistry getActionRegistry() {
		return new DemoActionRegistry(this, modifyAllowed);
	}

	protected ImporterRegistry getImporterRegistry() {
		return new DemoImporterRegistry(this);
	}
	public static void main(String[] args) {
		if (args.length > 0)
			new DemoFileAnalyzer(new File(args[0]), false);		
		else
			new DemoFileAnalyzer(null, false);		
	}

}
