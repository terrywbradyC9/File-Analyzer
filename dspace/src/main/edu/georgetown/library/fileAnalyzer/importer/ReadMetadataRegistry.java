package edu.georgetown.library.fileAnalyzer.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import com.google.gson.Gson;


import gov.nara.nwts.ftapp.ActionResult;
import gov.nara.nwts.ftapp.FTDriver;
import gov.nara.nwts.ftapp.Timer;
import gov.nara.nwts.ftapp.importer.DefaultImporter;
import gov.nara.nwts.ftapp.stats.Stats;
import gov.nara.nwts.ftapp.stats.StatsGenerator;
import gov.nara.nwts.ftapp.stats.StatsItem;
import gov.nara.nwts.ftapp.stats.StatsItemConfig;
import gov.nara.nwts.ftapp.stats.StatsItemEnum;

/**
 * Importer for tab delimited files
 * 
 * @author TBrady
 * 
 */
public class ReadMetadataRegistry extends DefaultImporter {
    class Field {
        private String name;
        private String element;
        private String qualifier;
        private String description;
        
        public String name() {return name;}
        public String element() {return element;}
        public String qualifier() {return qualifier;}
        public String description() {return description;}
        
        public void setName(String name) {this.name = name;}
        public void setElement(String element) {this.name = element;}
        public void setQualifier(String qualifier) {this.name = qualifier;}
        public void setDescription(String description) {this.name = description;}
        
        

        public String toString() {
            return String.format("name:%s,element:%s,qualifier:%s,description:%s", name, element, qualifier, description == null ? null : description.replaceAll("\\s+", " "));
        }
    }
    class Schema {
        private String prefix;
        private String namespace;
        private List<Field> fields;

        public String prefix() {return prefix;}
        public String namespace() {return namespace;}
        public List<Field> getFields() {return fields;}

        public void setPrefix(String prefix) {this.prefix = prefix;}
        public void setNamespace(String namespace) {this.namespace = namespace;}
        public void setFields(List<Field> fields) {this.fields = fields;}
        
        public Schema() {
            System.err.println("Foo\n");
        }
        
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("prefix:%s,namespace:%s", prefix, namespace));
            for(Field f: fields) {
                sb.append(String.format("%n\t%s",f));
            }
            return sb.toString();
        }
    }
    
	public static enum MetadataStatsItems implements StatsItemEnum {
		Name(StatsItem.makeStringStatsItem("Field Name", 150)),
        Schema(StatsItem.makeStringStatsItem("Schema", 60).makeFilter(true)),
        Element(StatsItem.makeStringStatsItem("Element", 100).makeFilter(true)),
        Qualifier(StatsItem.makeStringStatsItem("Qualifier", 100)),
        Description(StatsItem.makeStringStatsItem("Description", 460)),
        ;

		StatsItem si;

		MetadataStatsItems(StatsItem si) {
			this.si = si;
		}

		public StatsItem si() {
			return si;
		}
	}

	public static enum Generator implements StatsGenerator {
		INSTANCE;
		public Stats create(String key) {
			return new Stats(details, key);
		}
	}

	public static StatsItemConfig details = StatsItemConfig
			.create(MetadataStatsItems.class);
	public ReadMetadataRegistry(FTDriver dt) {
		super(dt);

	}

	public String toString() {
		return "Read DSpace Metadata Registry";
	}

	public String getDescription() {
		return "This rule will read the DSpace Metadata Registry saved as a JSON File.";
	}

	public String getShortName() {
		return "MetadataReg";
	}

	public ActionResult importFile(File selectedFile) throws IOException {
		Timer timer = new Timer();
		TreeMap<String, Stats> types = new TreeMap<String, Stats>();
        Schema[] data = new Gson().fromJson(new FileReader(selectedFile), Schema[].class);
        for(Schema s: data){
            for(Field f:s.fields) {
                Stats stats = Generator.INSTANCE.create(f.name);
                stats.setVal(MetadataStatsItems.Schema, s.prefix);
                stats.setVal(MetadataStatsItems.Element, f.element);
                stats.setVal(MetadataStatsItems.Qualifier, f.qualifier == null ? "" : f.qualifier);
                stats.setVal(MetadataStatsItems.Description, f.description == null ? "" : f.description);
                types.put(f.name, stats);
            }
        }
        
        details.createFilters(types);
		return new ActionResult(selectedFile, selectedFile.getName(),
				this.toString(), details, types, true, timer.getDuration());
	}
}
