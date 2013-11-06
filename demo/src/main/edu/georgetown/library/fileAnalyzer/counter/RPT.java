package edu.georgetown.library.fileAnalyzer.counter;

public class RPT implements Comparable<RPT> {

	public String name;
	public REV rev;
	String key;
	
	RPT(String name, REV rev) {
		this.name = name;
		this.rev = rev;
		this.key = name + " " + rev.name();
	}
	
    @Override  
    public int compareTo(RPT other)  
    {  
        return key.compareTo(other.key);  
    }  
      
    @Override  
    public boolean equals(Object other)  
    {  
        return (other != null) && (getClass() == other.getClass()) &&   
            key.equals(((RPT)other).key);  
    }  
      
    @Override  
    public int hashCode()  
    {  
        return key.hashCode();  
    }  
      
    @Override  
    public String toString()  
    {  
        return key;  
    }  
    
	static RPT createRPT(String name, String version) {
		for(REV rev: REV.values()) {
			if (rev.name().equals(version)) {
				return new RPT(name, rev);
			}
		}
		return null;
	}
	

}
