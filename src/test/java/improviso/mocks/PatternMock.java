package improviso.mocks;

import improviso.*;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author User
 */
public class PatternMock extends Pattern {
    private ArrayList<Note> notes = new ArrayList<>();
    private Integer nextDuration;
    private int executions = 0;
    
    public static class PatternMockBuilder extends PatternBuilder {
        public PatternMockBuilder() {
            super();
        }
        
        @Override
        public PatternMock build() {
            return new PatternMock(this);
        }
    }
    
    public static class PatternExecutionMock extends PatternExecution {
        final private PatternMock patternMock;
        
        public PatternExecutionMock(PatternMock pattern, int length) {
            super(pattern, length);
            this.patternMock = pattern;
        }
        
        @Override
        public MIDINoteList execute(Random random, double finalPosition, int length) {
            MIDINoteList list = new MIDINoteList();
            this.patternMock.getNotes().forEach((note) -> {
                list.addAll(note.execute(random, this.getLength(), finalPosition, length));
            });
            this.patternMock.addExecution();

            return list;
        }
    }
    
    private PatternMock(PatternMockBuilder builder) {
        super(builder);
    }
    
    public void setNextDuration(Integer duration) {
        this.nextDuration = duration;
    }
    
    public void resetExecutions() {
        this.executions = 0;
    }
    
    public void setNotes(ArrayList<Note> list) {
        this.notes = list;
    }
    
    public ArrayList<Note> getNotes() {
        return this.notes;
    }
    
    @Override
    public PatternExecution initialize(Random random) {
        return new PatternExecutionMock(this, this.nextDuration);
    }
    
    public void addExecution() {
        this.executions++;
    }
    
    public int getExecutions() {
        return this.executions;
    }
}
