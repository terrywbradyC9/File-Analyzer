package edu.georgetown.library.fileAnalyzer.filetest;

import gov.loc.repository.bagit.Bag;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filter.ZipFilter;
import gov.nara.nwts.ftapp.ftprop.InitializationStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import edu.georgetown.library.fileAnalyzer.util.InvalidMetadataException;
import edu.georgetown.library.fileAnalyzer.util.APTrustHelper;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class AIPZipToAPT extends AIPToAPT { 
	
    public AIPZipToAPT(FTDriver dt) {
        super(dt);
    }

	public String getKey(File f) {
		return f.getName();
	}
    public String toString() {
        return "Package AIP Zip for APT";
    }
    public String getShortName(){return "AIP Zip->APT";}
    
    public String getDescription() {
        return "This rule will package a DSpace AIP Zip package for APTrust";
    }
    
    @Override public boolean isTestDirectory() {
    	return false;
    }
    @Override public boolean processRoot() {
        return false;
    }

    @Override public boolean isTestFiles() {
        return true; 
    }

    @Override public boolean isTestable(File f) {
    	return f.getName().toLowerCase().endsWith(".zip");
    }

	public void initFilters() {
		filters.add(new ZipFilter());
	}

	File outdir;

	public static final String AIPEXTRACT = "aipextract_";
	public static final String METSXML = "mets.xml";
	
	@Override public InitializationStatus init() {
		InitializationStatus istat = super.init();
		try {
			Path outpath = Files.createTempDirectory(AIPEXTRACT);
			outdir = outpath.toFile();
			outdir.deleteOnExit();
		} catch (IOException e) {
			istat.addFailMessage(e.getMessage());
		}
		return istat;
	}
	
	@Override public void cleanup(int count) {
		if (outdir != null) {
			outdir.delete();
		}
	}
	
	@Override public int fillBag(File f, APTrustHelper aptHelper) throws FileNotFoundException, IOException, InvalidMetadataException{
        byte[] buf = new byte[4096];
        File zout = outdir;
        Bag bag = aptHelper.getBag();
        int count = 0;

        try(
            ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
        ) {
            for(ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry()){
                if (ze.getName().startsWith("__MACOSX")) continue;
                if (ze.getName().endsWith(".DS_Store")) continue;
                
                File ztemp = new File(ze.getName());
                File zeout = new File(zout, ztemp.getName());
                if (ze.isDirectory()) continue;
                
                try(FileOutputStream fos = new FileOutputStream(zeout)) {
                    for(int i=zis.read(buf); i > -1; i=zis.read(buf)) {
                        fos.write(buf, 0, i);
                    }
                }
                
                if (ze.getName().equals(METSXML)) {
                    aptHelper.parseMetsFile(zeout);
                }
                
                bag.addFileToPayload(zeout);
                count++;
            }
        }
        return count;
	}
	
}
