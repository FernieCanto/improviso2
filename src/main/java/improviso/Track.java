package improviso;

import java.util.*;
/**
 * RESPONSABILIDADES DA TRILHA
 *  - Buscar próximo padrão e devolver noteDefinitions (todas, ou com determinada duração)
 *  - Identificar corretamente o fim de sua execução
 * @author fernando
 */
public class Track implements java.io.Serializable {
    private final String id;
    private final Group rootGroup;
    
    private Pattern.PatternExecution currentExecution;
    private int currentPosition;
    
    private Integer positionFinished = null;
    private Integer positionInterrupt = null;
    
    public static class TrackBuilder {
        private String id;
        private Group rootGroup;
        
        public String getId() {
            return this.id;
        }
        
        public TrackBuilder setId(String id) {
            this.id = id;
            return this;
        }
        
        public Group getRootGroup() {
            return this.rootGroup;
        }
        
        public TrackBuilder setRootGroup(Group rootGroup) {
            this.rootGroup = rootGroup;
            return this;
        }
        
        public Track build() {
            return new Track(this);
        }
    }
    
    protected Track(TrackBuilder builder) {
        this.id = builder.getId();
        this.rootGroup = builder.getRootGroup();
    }
    
    public String getId() {
        return this.id;
    }
    
    /**
     * Prepares the Track for a new execution of its Section, updating its
     * current position and resetting its Group tree.
     */
    public void initialize() {
        this.currentPosition = 0;
        this.rootGroup.resetGroup();
        this.positionFinished = null;
        this.positionInterrupt = null;
    }
    
    /**
     * Recovers the next Pattern to be executed by sending a message to the
     * root Group of the Track. The Message produced by the Groups will be
     * returned
     * @param rand
     */
    public void selectNextPattern(Random rand) {
        this.currentExecution = this.rootGroup.execute(rand);
    }
    
    /**
     * Get the Track's current position within the Composition.
     * @return Position in ticks
     */
    public int getCurrentPosition() {
        return this.currentPosition;
    }
    
    public GroupMessage getMessage() {
        return this.rootGroup.getMessage();
    }
    
    /**
     * Obtains the ending position of the currently selected Pattern.
     * @return 
     */
    public int getEnd() {
        return this.currentPosition + this.currentExecution.getLength();
    }
    
    public Integer getPositionFinished() {
        return this.positionFinished;
    }
    
    public Integer getPositionInterrupt() {
        return this.positionInterrupt;
    }
    
    /**
     * Executes the last selected Pattern of the Track, given the Track's
     * current position within the Section and the maximum allowed duration for
     * the Pattern, receiving the list of Notes produced by the Pattern.
     * @param random
     * @param sectionEnd
     * @param interruptTracks
     * @param calculatePatternPosition
     * @return Sequência de noteDefinitions geradas.
     */
    public MIDINoteList execute(Random random, Section.SectionEnd sectionEnd, boolean interruptTracks, boolean calculatePatternPosition) {
        MIDINoteList result = this.currentExecution.execute(
                random,
                calculatePatternPosition ? this.getRelativePatternPosition(sectionEnd) : 0.0d,
                this.getMaximumPatternLength(sectionEnd, interruptTracks)
        ).offsetNotes(currentPosition);
        this.currentPosition = this.getEnd();
        if (interruptTracks && sectionEnd.compareTo(currentPosition) < 0) {
            this.currentPosition = sectionEnd.intValue();
        }
        
        this.processMessage();
        
        return result;
    }
    
    private void processMessage() {
        if (this.rootGroup.getMessage().getInterrupt()) {
            if (this.positionInterrupt == null) {
                this.positionInterrupt = this.currentPosition;
            }
        } else if (this.rootGroup.getMessage().getFinished()) {
            if (this.positionFinished == null) {
                this.positionFinished = this.currentPosition;
            }
        }
    }
    
    private double getRelativePatternPosition(Section.SectionEnd sectionEnd) {
        if (!sectionEnd.endIsKnown()) {
            return 0.0d;
        } else if (this.getEnd() < sectionEnd.intValue()) {
            return ((double)(this.getEnd()) / (double)(sectionEnd.intValue()));
        } else {
            return 1.0d;
        }
    }
    
    private Integer getMaximumPatternLength(Section.SectionEnd sectionEnd, boolean interruptTracks) {
        if (!sectionEnd.endIsKnown() || !interruptTracks) {
            return Integer.MAX_VALUE;
        } else {
            return sectionEnd.intValue() - this.getCurrentPosition();
        }
    }
}