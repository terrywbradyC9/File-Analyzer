package gov.nara.nwts.ftapp.ftprop;

public class InitializationStatus {
    private boolean showMessage = false;
    private boolean failTest = false;
    private StringBuilder sb = new StringBuilder();
    
    public InitializationStatus() {
    }
    
    public boolean hasMessage() {return showMessage;}
    public boolean hasFailTest() {return failTest;}
    public String getMessage() {return sb.toString();}
    
    public void addMessage(String s) {
        sb.append(s);
        showMessage = true;
    }
    public void addFailMessage(String s) {
        sb.append(s);
        showMessage = true;
        failTest = true;
    }
    public void addMessage(InitializationStatus iStat) {
        if (iStat.hasFailTest()) {
            addFailMessage(iStat.getMessage());
        } else if (iStat.hasMessage()) {
            addMessage(iStat.getMessage());
        }
    }
    public void addMessage(Exception ex) {
        addFailMessage(ex.getMessage());
    }
    
}
