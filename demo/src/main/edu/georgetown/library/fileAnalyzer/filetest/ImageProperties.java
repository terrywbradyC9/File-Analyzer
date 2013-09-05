package edu.georgetown.library.fileAnalyzer.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filter.JpegFileTestFilter;
import gov.nara.nwts.ftapp.filter.TiffFileTestFilter;
import gov.nara.nwts.ftapp.filter.TiffJpegFileTestFilter;
import gov.nara.nwts.ftapp.ftprop.FTPropString;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.parser.image.ImageMetadataExtractor;

/**
 * Extract all metadata fields from a TIF or JPG using categorized tag defintions.
 * @author TBrady
 *
 */
class ImageProperties extends DefaultFileTest { 
	public static enum SPEC{
		IN_SPEC,
		NOT_IN_SPEC,
		;
	}
	
	
	private static enum TiffStatsItems implements StatsItemEnum {
		Key(StatsItem.makeStringStatsItem("Path", 350)),
		Spec(StatsItem.makeEnumStatsItem(SPEC.class, "In Spec?")),
		Height(StatsItem.makeIntStatsItem("Height")),
		Width(StatsItem.makeIntStatsItem("Width")),
		BitsPerSample(StatsItem.makeStringStatsItem("BitsPerSample")),
		;
		
		StatsItem si;
		TiffStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		class PagesStats extends Stats {
			public PagesStats(String key) {
				super(details, key);
			}

		}
		public PagesStats create(String key) {return new PagesStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(TiffStatsItems.class);

	long counter = 1000000;
	
	public static final String H_MAX = "height-max";
	public static final String H_MIN = "height-min";
	public static final String W_MAX = "width-max";
	public static final String W_MIN = "width-min";
	
	public ImageProperties(FTDriver dt) {
		super(dt);
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  H_MIN, H_MIN,
				"Minimum Height in pixels or blank to ignore", ""));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  H_MAX, H_MAX,
				"Maximum Height in pixels or blank to ignore", ""));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  W_MIN, W_MIN,
				"Minimum Width in pixels or blank to ignore", ""));
		ftprops.add(new FTPropString(dt, this.getClass().getSimpleName(),  W_MAX, W_MAX,
				"Maximum Width in pixels or blank to ignore", ""));
	}

	public String toString() {
		return "Image Spec";
	}
	public String getKey(File f) {
		return f.getPath();
	}
	
    public String getShortName(){return "Image";}

    
	public Object fileTest(File f) {
		Stats s = getStats(f);
		try {
			Metadata metadata = new Metadata();
			ImageMetadataExtractor ime = new ImageMetadataExtractor(metadata);
			
			if (f.getName().toLowerCase().endsWith("tif") || f.getName().toLowerCase().endsWith("tiff")) {
				ime.parseTiff(f);
				
				s.setVal(TiffStatsItems.Height, metadata.getInt(TIFF.IMAGE_LENGTH));
				s.setVal(TiffStatsItems.Width, metadata.getInt(TIFF.IMAGE_WIDTH));
				s.setVal(TiffStatsItems.BitsPerSample, metadata.get(TIFF.BITS_PER_SAMPLE).toString());				
			} else if (f.getName().toLowerCase().endsWith("jpg") || f.getName().toLowerCase().endsWith("jpeg")) {
				ime.parseJpeg(f);
				
				s.setVal(TiffStatsItems.Height, metadata.getInt(TIFF.IMAGE_LENGTH));
				s.setVal(TiffStatsItems.Width, metadata.getInt(TIFF.IMAGE_WIDTH));
				s.setVal(TiffStatsItems.BitsPerSample, metadata.get(TIFF.BITS_PER_SAMPLE).toString());				
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append(s.getVal(TiffStatsItems.Height));
		buf.append(" x ");
		buf.append(s.getVal(TiffStatsItems.Width));
		buf.append("; ");
		buf.append(s.getVal(TiffStatsItems.BitsPerSample));
		buf.append(" bits per sample ");
		
		testVal(s, (Integer)s.getVal(TiffStatsItems.Height), H_MIN, H_MAX);
		testVal(s, (Integer)s.getVal(TiffStatsItems.Width), W_MIN, W_MAX);

		return buf.toString();
	}
	
	public void testVal(Stats stat, int val, String keyMin, String keyMax) {
		String max = this.getProperty(keyMax, "").toString();
		String min = this.getProperty(keyMin, "").toString();
		if (!max.isEmpty()) {
			try {
				int imax = Integer.parseInt(max);
				if (val > imax) {
					stat.setVal(TiffStatsItems.Spec, SPEC.NOT_IN_SPEC);
				}
			} catch (NumberFormatException e) {
			}
		}
		if (!min.isEmpty()) {
			try {
				int imin = Integer.parseInt(min);
				if (val < imin) {
					stat.setVal(TiffStatsItems.Spec, SPEC.NOT_IN_SPEC);
				}
			} catch (NumberFormatException e) {
			}
		}
		
	}
	
    public Stats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details; 
    }

	public void initFilters() {
		filters.add(new TiffJpegFileTestFilter());
		filters.add(new TiffFileTestFilter());
		filters.add(new JpegFileTestFilter());
	}

	public String getDescription() {
		return "";
	}

}
