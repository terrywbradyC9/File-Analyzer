package edu.georgetown.library.fileAnalyzer.util;

import java.io.File;
import java.io.IOException;

import gov.loc.repository.bagit.writer.impl.ZipWriter;

public class ZipBagHelper extends FABagHelper {
    
    public ZipBagHelper(File source) {
    	super(source);
    }
    
    @Override public void createBagFile() throws IncompleteSettingsExcpetion {
    	validate();
		newBag = new File(parent, source.getName() + "_bag.zip");
    }
    
    @Override public void writeBagFile() throws IOException, IncompleteSettingsExcpetion {
    	validate();
    	if (newBag == null) throw new IncompleteSettingsExcpetion("Bag File must be created - call createBagFile()");
	    ZipWriter writer = new ZipWriter(bf); 
	    writer.setBagDir(source.getName());
	    bag.write(writer, newBag);
	    bag.close();
    }
    
}
