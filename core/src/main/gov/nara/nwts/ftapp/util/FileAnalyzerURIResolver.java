package gov.nara.nwts.ftapp.util;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class FileAnalyzerURIResolver implements URIResolver {
	public Source resolve(String href, String base)	throws TransformerException {
		InputStream is = FileAnalyzerURIResolver.class.getClassLoader().getResourceAsStream(href);
		if (is == null) is = FileAnalyzerURIResolver.class.getClassLoader().getResourceAsStream("resources" + href);
		return new StreamSource(is);
	}		
	public static FileAnalyzerURIResolver INSTANCE = new FileAnalyzerURIResolver();

}
