package gov.nara.nwts.ftappImg.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filetest.FileTest;
import gov.nara.nwts.ftapp.filter.JpegFileTestFilter;
import gov.nara.nwts.ftappImg.jpeg.JpegExtractor;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

/**
 * Extract key metadata from a JPG file; this implementation has been superceeded by better implementations
 * @author TBrady
 *
 */
class CountJpeg extends DefaultFileTest { 

	private static enum JpegCountStatsItems implements StatsItemEnum {
		File(StatsItem.makeStringStatsItem("File", 200)),
		BitsPerSample(StatsItem.makeIntStatsItem("tiff:BitsPerSample")),
		ColorSpace(StatsItem.makeStringStatsItem("Color Space", 200)),
		Headline(StatsItem.makeStringStatsItem("Headline", 300)),
		Caption(StatsItem.makeStringStatsItem("Caption/Abstract", 200)),
		Keywords(StatsItem.makeStringStatsItem("Keywords", 200)),

		XResolution(StatsItem.makeStringStatsItem("X Resolution", 200)),
		YResolution(StatsItem.makeStringStatsItem("Y Resolution", 200)),

		TiffXResolution(StatsItem.makeFloatStatsItem("tiff:X Resolution").setWidth(200)),
		TiffYResolution(StatsItem.makeFloatStatsItem("tiff:Y Resolution").setWidth(200)),
		TiffImageWidth(StatsItem.makeStringStatsItem("tiff:ImageWidth", 200)),
		TiffImageHeigth(StatsItem.makeStringStatsItem("tiff:ImageHeight", 200)),

		ImageWidth(StatsItem.makeStringStatsItem("Image Width", 200)),
		ImageHeight(StatsItem.makeStringStatsItem("Image Height", 200)),
		ExifImageWidth(StatsItem.makeStringStatsItem("ExifImage Width", 200)),
		ExifImageHeigth(StatsItem.makeStringStatsItem("Exif Image Height", 200)),
		;
		
		StatsItem si;
		JpegCountStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static enum Generator implements StatsGenerator {
		INSTANCE;

		private class JpegStats extends Stats {

			public JpegStats(String key) {
				super(details, key);
			}
			
			
			public Object compute(File f, FileTest fileTest) {
				JpegExtractor jext = new JpegExtractor(f);
				setVal(JpegCountStatsItems.BitsPerSample,jext.getInt("tiff:BitsPerSample", 0));
				setVal(JpegCountStatsItems.ColorSpace, jext.getAttribute("Color Space"));
				setVal(JpegCountStatsItems.Headline, jext.getAttribute("Headline"));
				setVal(JpegCountStatsItems.Caption, jext.getAttribute("Caption/Abstract"));
				setVal(JpegCountStatsItems.Keywords, jext.getAttribute("Keywords"));

				setVal(JpegCountStatsItems.XResolution, jext.getAttribute("X Resolution"));
				setVal(JpegCountStatsItems.YResolution, jext.getAttribute("Y Resolution"));

				setVal(JpegCountStatsItems.TiffXResolution, jext.getFloat("tiff:XResolution", 0));
				setVal(JpegCountStatsItems.TiffYResolution, jext.getFloat("tiff:YResolution", 0));
				setVal(JpegCountStatsItems.TiffImageWidth, jext.getAttribute("tiff:ImageWidth"));
				setVal(JpegCountStatsItems.TiffImageHeigth, jext.getAttribute("tiff:ImageLength"));

				setVal(JpegCountStatsItems.ImageWidth, jext.getAttribute("Image Width"));
				setVal(JpegCountStatsItems.ImageHeight, jext.getAttribute("Image Height"));
				setVal(JpegCountStatsItems.ExifImageWidth, jext.getAttribute("Exif Image Width"));
				setVal(JpegCountStatsItems.ExifImageHeigth, jext.getAttribute("Exif Image Height"));
				jext.close();
				return fileTest.fileTest(f);
			}
		}
		public JpegStats create(String key) {return new JpegStats(key);}
	}

	public static StatsItemConfig details = StatsItemConfig.create(JpegCountStatsItems.class);
	public CountJpeg(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "Jpeg Properties";
	}
	public String getKey(File f) {
		return f.getName();
	}
	
    public String getShortName(){return "Jpeg";}

	public Object fileTest(File f) {
		return null;
	}
    public Stats createStats(String key){ 
    	return Generator.INSTANCE.create(key);
    }
    public StatsItemConfig getStatsDetails() {
    	return details;
    }

	public void initFilters() {
		filters.add(new JpegFileTestFilter());
	}

	public String getDescription() {
		return "This test will extract key metadata items from each Jpeg file that is found";
	}

}
