package gov.nara.nwts.ftapp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XMLUtil {
	public static DocumentBuilderFactory dbf;
	public static DocumentBuilder db;
	public static TransformerFactory tf;
	
	static {
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			tf = TransformerFactory.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static void serialize(Document d, File f) {
		try {
			doSerialize(d, f);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void doSerialize(Document d, File f) throws TransformerException, IOException {
		Transformer t = tf.newTransformer();
		FileOutputStream fos = new FileOutputStream(f);
		StreamResult sr = new StreamResult(fos);
		t.transform(new DOMSource(d), sr);
		fos.close();
	}

}
