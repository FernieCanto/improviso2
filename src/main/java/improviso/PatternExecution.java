package improviso;

import java.util.Random;

/**
 *
 * @author FernieCanto
 */
public class PatternExecution {
    
    private final Pattern pattern;
    private final int length;

    public PatternExecution(Pattern pattern, int length) {
        this.pattern = pattern;
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public MIDINoteList execute(Random random, double finalPosition, int length) {
        MIDINoteList noteList = new MIDINoteList();
        this.pattern.getNoteIterator().forEachRemaining((note) -> {
            noteList.addAll(note.execute(random, this.length, finalPosition, length));
        });
        return noteList;
    }

    public MIDINoteList executeRange(Random random, int patternStart, int patternEnd, double finalPosition, int length) {
        MIDINoteList noteList = new MIDINoteList();
        this.pattern.getNoteIterator().forEachRemaining((note) -> {
            noteList.addAll(note.executeRange(random, patternStart, patternEnd, this.length, finalPosition, length));
        });
        return noteList;
    }
    
}
