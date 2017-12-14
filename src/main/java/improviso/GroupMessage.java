package improviso;

/**
 *
 * @author fernando
 */
public class GroupMessage {
    private final String origin;
    private boolean finished = false;
    private boolean disableTrack = false;
    private boolean interrupt = false;
    private boolean interruptSection = false;
    
    public GroupMessage(String origin) {
        this.origin = origin;
    }
    
    public void setFinished() {
        this.finished = true;
    }
    
    public void setDisable() {
        this.disableTrack = true;
    }
    
    public void setInterrupt() {
        this.interrupt = true;
    }
    
    public void setInterruptSection() {
        this.interruptSection = true;
    }

    public boolean getFinished() {
        return finished;
    }

    public boolean getDisableTrack() {
        return disableTrack;
    }

    public boolean getInterrupt() {
        return interrupt;
    }

    public boolean getInterruptSection() {
        return interruptSection;
    }
    
    public String getOrigin() {
        return this.origin;
    }
    
    @Override
    public String toString() {
        return (getFinished() ? "Signal" : "Not Signal") + ", " + (getInterrupt() ? "Finish" : "Not finish");
    }
}
