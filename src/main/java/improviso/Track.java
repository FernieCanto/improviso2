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
    
    private PatternExecution currentExecution;
    private GroupMessage message;
    private int currentPosition;
    private int currentRealTimePosition;
    private int currentPositionInPattern;
    
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
     * @param random
     */
    public void initialize(Random random) {
        this.currentPosition = 0;
        this.currentRealTimePosition = 0;
        this.currentPositionInPattern = 0;
        this.rootGroup.resetGroup();
        this.positionFinished = null;
        this.positionInterrupt = null;
        this.getNextPatternExecution(random);
    }
    
    /**
     * Recovers the next Pattern to be executed by sending a message to the
     * root Group of the Track. The Message produced by the Groups will be
     * returned
     * @param rand
     */
    private void getNextPatternExecution(Random rand) {
        this.currentExecution = this.rootGroup.execute(rand);
        this.message = this.rootGroup.getMessage();
    }
    
    /**
     * Get the Track's current position within the Composition.
     * @return Position in ticks
     */
    public int getCurrentPosition() {
        return this.currentPosition;
    }
    
    public int getCurrentRealTimePosition() {
        return this.currentRealTimePosition;
    }
    
    public GroupMessage getMessage() {
        return this.message;
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
     * @return List of generated note definitions.
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
        this.getNextPatternExecution(random);
        
        return result;
    }

    public MIDINoteList executeTicks(Random random, Section.SectionEnd sectionEnd, int ticks, boolean interruptTracks, boolean calculatePatternPosition) {
        int remainingTicks = ticks;
        MIDINoteList result = new MIDINoteList();
        
        while (remainingTicks > 0) {
            MIDINoteList list = this.currentExecution.executeRange(
                    random,
                    this.currentPositionInPattern,
                    this.currentPositionInPattern + remainingTicks - 1,
                    calculatePatternPosition ? this.getRelativePatternPosition(sectionEnd) : 0.0d,
                    this.getMaximumPatternLength(sectionEnd, interruptTracks)
            ).offsetNotes(currentPosition);
            result.addAll(list);
            
            if ( (this.currentPositionInPattern + remainingTicks) >= this.currentExecution.getLength()) {
                this.currentPosition = this.getEnd();
                remainingTicks -= (this.currentExecution.getLength() - this.currentPositionInPattern);
                this.processMessage();
                this.getNextPatternExecution(random);
                this.currentPositionInPattern = 0;
            } else {
                this.currentPositionInPattern += remainingTicks;
                remainingTicks = 0;
            }
        }
        
        this.currentRealTimePosition += ticks;
        return result;
    }
    
    private void processMessage() {
        if (this.message.getInterrupt()) {
            if (this.positionInterrupt == null) {
                this.positionInterrupt = this.currentPosition;
            }
        } else if (this.message.getFinished()) {
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