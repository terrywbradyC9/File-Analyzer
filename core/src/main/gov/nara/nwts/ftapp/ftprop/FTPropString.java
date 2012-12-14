package gov.nara.nwts.ftapp.ftprop;

import gov.nara.nwts.ftapp.FTDriver;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * File Test Property object for string values
 * @author TBrady
 *
 */

public class FTPropString extends DefaultFTProp {
	JTextField tf;
	public FTPropString(FTDriver ft, String name, String shortname, String description, Object def) {
		super(ft, name, shortname, description, def);
		init();
		tf = new JTextField(this.def.toString());
		tf.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent arg0) {
				if (FTPropString.this.ft.hasPreferences()) {
					FTPropString.this.ft.getPreferences().put(getPrefString(), tf.getText());
				}
			}

			public void insertUpdate(DocumentEvent arg0) {
				if (FTPropString.this.ft.hasPreferences()) {
					FTPropString.this.ft.getPreferences().put(getPrefString(), tf.getText());
				}
			}

			public void removeUpdate(DocumentEvent arg0) {
				if (FTPropString.this.ft.hasPreferences()) {
					FTPropString.this.ft.getPreferences().put(getPrefString(), tf.getText());
				}
			}
		});
	}

	public JComponent getEditor() {
		return tf;
	}

	public Object getValue() {
		return tf.getText();
	}

	public void setValue(Object obj) {
		tf.setText(obj.toString());
	}
	public Object validate(String s) {
		if (s == null) s = "";
		return getValue();
	}


}
