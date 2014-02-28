package gov.nara.nwts.ftapp.importer;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Abstract class handling the import of a character-delimited text file allowing for individual values to be wrapped by quotation marks.
 * @author TBrady
 *
 */
public class DelimitedFileWriter {
	BufferedWriter bw;
	String sep;
	
	public static final String LF = "\n";
	public static final String CRLF = "\r\n";
	String lineend;

	public DelimitedFileWriter(File f, String sep) throws FileNotFoundException, UnsupportedEncodingException {
		this(f, sep, LF);
	}
	public DelimitedFileWriter(File f, String sep, String lineend) throws FileNotFoundException, UnsupportedEncodingException {
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
		this.sep = sep;
		this.lineend = lineend;
	}

	public DelimitedFileWriter(OutputStream os, String sep) throws FileNotFoundException, UnsupportedEncodingException {
		this(os, sep, LF);
	}
	public DelimitedFileWriter(OutputStream os, String sep, String lineend) throws FileNotFoundException, UnsupportedEncodingException {
		bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		this.sep = sep;
		this.lineend = lineend;
	}

	public void writeField(String s) throws IOException {
		writeField(s, false);
	}
	public void writeField(String s, boolean isLast) throws IOException {
		if (s==null) s= "";
		if (s.contains(sep)) {
			s = '"' + s.replaceAll("\"", "\"\"") + '"';
		}
		bw.write(s);
		bw.write(isLast ? lineend : sep);
	}
	public void writeRow(Vector<String> row) throws IOException {
		int i=0;
		for(String s: row) {
			i++;
			writeField(s, i == row.size());
		}
	}
	public void writeData(Vector<Vector<String>> data) throws IOException {
		for(Vector<String> row: data) writeRow(row);
		bw.close();
	}

    public static void writeFile(File f, String sep, Vector<Vector<String>> data) throws IOException {
    	DelimitedFileWriter dfw = new DelimitedFileWriter(f, sep);
    	dfw.writeData(data);
    }

    public static void writeCsv(File f, Vector<Vector<String>> data) throws IOException {
    	writeFile(f, ",", data);
    }

	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
