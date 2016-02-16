package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.nara.nwts.ftapp.FTDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.georgetown.library.fileAnalyzer.util.InvalidMetadataException;
import edu.georgetown.library.fileAnalyzer.util.APTrustHelper;


/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class AIPDirToAPT extends AIPToAPT { 
    public AIPDirToAPT(FTDriver dt) {
        super(dt);
    }

    public String toString() {
        return "Package AIP Dir for APT";
    }
    public String getShortName(){return "AIP Dir->APT";}
    
    public String getDescription() {
        return "This rule will package a DSpace AIP Directory for APTrust";
    }
    
    @Override public boolean isTestDirectory() {
        return true;
    }

    @Override public boolean isTestable(File f) {
        return f.equals(getRoot());
    }

    @Override public boolean isTestFiles() {
        return false; 
    }
    
    @Override public boolean processRoot() {
        return true;
    }

    @Override
    public int fillBag(File f, APTrustHelper aptHelper) throws FileNotFoundException, IOException, InvalidMetadataException {
        Bag bag = aptHelper.getBag();
        for(File cf: f.listFiles()) {
            bag.addFileToPayload(cf);
        }
        findMetadata(f, aptHelper);
        return bag.getPayload().size();
    }

    public void findMetadata(File f, APTrustHelper aptHelper) throws IOException, InvalidMetadataException {
        if (f.isDirectory()) {
            for(File cf: f.listFiles()) {
                findMetadata(cf, aptHelper);
            }
        } else {
            if (f.getName().equals(METSXML)) {
                aptHelper.parseMetsFile(f);
            }
        }
    }

}
