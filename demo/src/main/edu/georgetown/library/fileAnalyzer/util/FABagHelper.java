package edu.georgetown.library.fileAnalyzer.util;

import java.io.File;
import java.io.IOException;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

public class FABagHelper {
	public enum STAT {
		VALID,
		INVALID, 
		ERROR
	}
	
    public static final String P_INSTID = "inst-id";
    public static final String P_ITEMUID = "item-uid";
	public static final String P_SRCORG = "source-org";
    public static final String P_BAGTOTAL = "bag-total";
    public static final String P_BAGCOUNT = "bag-count";
    public static final String P_INTSENDDESC = "internal-sender-desc";
    public static final String P_INTSENDID = "internal-sender-id";
    public static final String P_TITLE = "title";
    public static final String P_ACCESS = "access";

    public class IncompleteSettingsExcpetion extends Exception {
		private static final long serialVersionUID = 1L;

		public IncompleteSettingsExcpetion(String message) {
    		super(message);
    	}
    };
    
    File parent;
    File source;
    File newBag;
    Bag bag;
    BagFactory bf;
    
    public FABagHelper(File source) {
    	this.source = source;
    	this.parent = source.getParentFile();
    	bf = new BagFactory();
    	bag = bf.createBag();
    }
    
    public void validate() throws IncompleteSettingsExcpetion {
    	StringBuilder sb = new StringBuilder();
    	validateImpl(sb);
    	if (sb.length() > 0) {
    		throw new IncompleteSettingsExcpetion(sb.toString());
    	}
    }
    public void validateImpl(StringBuilder sb) throws IncompleteSettingsExcpetion {
    }
        
    public void createBagFile() throws IncompleteSettingsExcpetion {
    	validate();
		newBag = new File(parent, source.getName() + "_bag");
    }
    
    public Bag getBag() {
    	return bag;
    }
    
    public void generateBagInfoFiles() throws IOException, IncompleteSettingsExcpetion {
    	validate();
    	if (newBag == null) throw new IncompleteSettingsExcpetion("Bag File must be created - call createBagFile()");
	    
	    DefaultCompleter comp = new DefaultCompleter(bf);
		
		comp.setGenerateBagInfoTxt(true);
		comp.setUpdateBaggingDate(true);
		comp.setUpdateBagSize(true);
		comp.setUpdatePayloadOxum(true);
		comp.setGenerateTagManifest(false);
		
		bag = comp.complete(bag);
    }
    
    public void writeBagFile() throws IOException, IncompleteSettingsExcpetion {
    	validate();
    	if (newBag == null) throw new IncompleteSettingsExcpetion("Bag File must be created - call createBagFile()");
	    Writer writer = new FileSystemWriter(bf); 
	    bag.write(writer, newBag);
	    bag.close();
    }
    
    public String getFinalBagName() throws IncompleteSettingsExcpetion {
    	validate();
    	if (newBag == null) throw new IncompleteSettingsExcpetion("Bag File must be created - call createBagFile() and writeBagFile()");
    	return newBag.getName();
    }
}
