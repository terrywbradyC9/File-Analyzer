package edu.georgetown.library.fileAnalyzer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;

public class TarUtil {
	public static void tarFolder(File folder) throws FileNotFoundException, IOException {
		File tarout = new File(folder.getParentFile(), folder.getName() + ".tar");
		try(TarArchiveOutputStream tar = new TarArchiveOutputStream(new FileOutputStream(tarout))) {
	        TarUtil.tarSubDirectory("", folder, tar);			
		}
	}

	public static void tarFolderAndDeleteFolder(File folder) throws FileNotFoundException, IOException {
		tarFolder(folder);
		FileUtils.deleteDirectory(folder);
	}
	
	public static void tarSubDirectory(String basePath, File dir, TarArchiveOutputStream tar) throws IOException {
	    byte[] buffer = new byte[4096];
	    File[] files = dir.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            String path = basePath + file.getName() + "/";
	            TarArchiveEntry arch = new TarArchiveEntry(path); 
	            tar.putArchiveEntry(arch);
	            tarSubDirectory(path, file, tar);
	        } else {
	        	String path = basePath + file.getName();
	            TarArchiveEntry arch = new TarArchiveEntry(path); 
	            arch.setSize(file.length());
	            tar.putArchiveEntry(arch);
	            try(FileInputStream fin = new FileInputStream(file)) {
		            int length;
		            while ((length = fin.read(buffer)) > 0) {
		                tar.write(buffer, 0, length);
		            }	            	
	            }
                tar.closeArchiveEntry();
	        }
	    }
	}
	
	public static File untar(File f) throws IOException {
		File temp = File.createTempFile(f.getName(), "untar");
		//TarArchiveEntry entry = tarInput.getNextTarEntry();
		//byte[] content = new byte[entry.getSize()];
		//LOOP UNTIL entry.getSize() HAS BEEN READ {
		//    tarInput.read(content, offset, content.length - offset);
		//}
		return temp;
	}
}