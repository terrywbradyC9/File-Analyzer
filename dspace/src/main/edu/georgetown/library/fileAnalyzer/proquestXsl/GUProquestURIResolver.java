package edu.georgetown.library.fileAnalyzer.proquestXsl;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class GUProquestURIResolver implements URIResolver {
	public Source resolve(String href, String base)	throws TransformerException {
		InputStream is = GUProquestURIResolver.class.getClassLoader().getResourceAsStream("edu/georgetown/library/fileAnalyzer/proquestXsl/" + href);
		if (is == null) is = GUProquestURIResolver.class.getClassLoader().getResourceAsStream("resources/edu/georgetown/library/fileAnalyzer/proquestXsl/" + href);
		return new StreamSource(is);
	}		
	public static GUProquestURIResolver INSTANCE = new GUProquestURIResolver();

}
