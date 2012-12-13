package gov.nara.nwts.ftappImg.filetest;

import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.filetest.DefaultFileTest;
import gov.nara.nwts.ftapp.filetest.FileTest;
import gov.nara.nwts.ftapp.filter.TiffFileTestFilter;
import gov.nara.nwts.ftappImg.tags.XMPExtractor;
import gov.nara.nwts.ftappImg.tags.ImageTags.TAGS;
import gov.nara.nwts.ftappImg.tif.TifExtractor;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

import java.io.File;

/**
 * Extract key metadata from a TIF file; this implementation has been superceeded by better implementations
 * @author TBrady
 *
 */
class CountTiff extends DefaultFileTest { 

	private static enum ImageStatsItems implements StatsItemEnum {
		File(StatsItem.makeStringStatsItem("File", 200)),
		BitsPerChannel(StatsItem.makeIntStatsItem("Bits/Channel (258)")),
		ColorSpace(StatsItem.makeIntStatsItem("Color Space (262)")),
		ICCProfile(StatsItem.makeStringStatsItem("ICC Profile")),
		Description(StatsItem.makeStringStatsItem("Description (270)", 300)),
		Keywords(StatsItem.makeStringStatsItem("Keywords (XMP)", 200)),
		Instruction(StatsItem.makeStringStatsItem("Keywords (XMP)", 200)),

		Desc1(StatsItem.makeStringStatsItem("Desc 1", 150)),
		Desc2(StatsItem.makeStringStatsItem("Desc 2", 150)),
		Desc3(StatsItem.makeStringStatsItem("Desc 3", 150)),
		Desc4(StatsItem.makeStringStatsItem("Desc 4", 150)),
		;
		
		StatsItem si;
		ImageStatsItems(StatsItem si) {this.si=si;}
		public StatsItem si() {return si;}
	}
	
	public static enum Generator implements StatsGenerator {
		INSTANCE;

		public class ImageStats extends Stats {

			public ImageStats(String key) {
				super(details, key);
			}
			
			public Object compute(File f, FileTest fileTest) {
				TifExtractor tiffext = new TifExtractor(f);
				setVal(ImageStatsItems.BitsPerChannel,tiffext.getTiffInt(TAGS.TIFF_BITS_PER_CHANNEL));
				setVal(ImageStatsItems.ColorSpace,tiffext.getTiffInt(TAGS.TIFF_COLOR_SPACE));
				setVal(ImageStatsItems.ICCProfile,tiffext.getXMP(XMPExtractor.XMP_ICC));
				String tfs = tiffext.getTiffString(TAGS.TIFF_DESCRIPTION);
				setVal(ImageStatsItems.Description,tfs);
				setVal(ImageStatsItems.Keywords, tiffext.getXMP(XMPExtractor.XMP_KEY));
				setVal(ImageStatsItems.Instruction, tiffext.getXMP(XMPExtractor.XMP_INSTR));

				String[] parts = tfs.split("(\\s\\s\\s+|\n)");
				for(int i=0; (i < parts.length) && (i <4); i++){
					if (i==0) setVal(ImageStatsItems.Desc1,parts[i]);
					if (i==1) setVal(ImageStatsItems.Desc2,parts[i]);
					if (i==2) setVal(ImageStatsItems.Desc3,parts[i]);
					if (i==3) setVal(ImageStatsItems.Desc4,parts[i]);
				}
				tiffext.close();
				return fileTest.fileTest(f);
			}
		}
		public ImageStats create(String key) {return new ImageStats(key);}
	}
	public static StatsItemConfig details = StatsItemConfig.create(ImageStatsItems.class);
	
	public CountTiff(FTDriver dt) {
		super(dt);
	}

	public String toString() {
		return "Tif Properties";
	}
	public String getKey(File f) {
		return f.getName();
	}
	
    public String getShortName(){return "Tif";}

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
		filters.add(new TiffFileTestFilter());
	}

	public String getDescription() {
		return "This test will extract key metadata items from each TIF file that is found";
	}

}
