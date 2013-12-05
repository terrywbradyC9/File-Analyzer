package edu.georgetown.library.fileAnalyzer.filetest;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.NameValidationTest;
import gov.nara.nwts.ftapp.nameValidation.CustomPattern;
import gov.nara.nwts.ftapp.nameValidation.DirAnalysis;
import gov.nara.nwts.ftapp.nameValidation.RenameDetails;
import gov.nara.nwts.ftapp.nameValidation.RenameStatus;
import gov.nara.nwts.ftapp.nameValidation.ValidPattern;
/**
 * Filename validation rule to ensure that filenames are lowercase.
 * @author TBrady
 *
 */
class YearbookNameValidationTest extends NameValidationTest {

	Pattern dirPatternFolder = Pattern.compile("^\\d\\d\\d\\d*$");
	Pattern dirPatternImage = Pattern.compile("gt_yearbooks_\\d\\d\\d\\d_(\\d\\d\\d).tif");
	Pattern dirPatternIgnore = Pattern.compile("^(Thumbs.db|.*_st.pdf)$");
	
	public YearbookNameValidationTest(FTDriver dt) {
		super(dt, new ValidPattern("^gt_yearbooks_\\d\\d\\d\\d_(\\d\\d\\d.tif|st.pdf)*$", false),null, "Yearbook","Yearbook");
		
		testPatterns.add(new CustomPattern("^Thumbs.db$", false) {
			public RenameDetails report(File f, Matcher m) {
				return new RenameDetails(RenameStatus.SKIP);
			}
			
		});
		
		dirTestPatterns.add(new CustomPattern(dirPatternFolder, false) {
			public RenameDetails report(File f, Matcher m) {
				RenameDetails det = DirAnalysis.analyze(f, dirPatternImage, 1,	dirPatternIgnore, false, false);
				return det;
			}

		});
		dirTestPatterns.add(new CustomPattern("^.*$", false) {
			public RenameDetails report(File f, Matcher m) {
				return new RenameDetails(RenameStatus.SKIP);
			}

		});
	}
	
	public String getDescription() {
		return "This test will check conformance with the Georgetown Digitization Yearbook project."
				+  getNameValidationDisclaimer(); 
	}

}
