package gov.nara.nwts.ftapp.ftprop;

import java.text.SimpleDateFormat;

import javax.swing.JFormattedTextField;

import gov.nara.nwts.ftapp.FTDriver;

public class FTPropDate extends FTPropString {
	public static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
	public FTPropDate(FTDriver ft, String prefix, String name,
			String shortname, String description, Object def) {
		super(ft, prefix, name, shortname, description + " (MM/dd/yyyy)", def);
	}

	public void createTextField() {
		tf = new JFormattedTextField(FTPropDate.df);
		tf.setText(this.def.toString());
	}
}
