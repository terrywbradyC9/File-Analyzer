package edu.georgetown.library.fileAnalyzer.filetest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class TestGson {

    public static void main(String[] args) {
        try {
            Reader reader = new FileReader(new File("C:\\Users\\twb27\\Downloads\\metadataRegistry.json"));
            Schema[] data = new Gson().fromJson(reader, Schema[].class);
            for(Schema s: data){System.out.println(s.toString());}
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    
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
    
}
