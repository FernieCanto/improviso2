package improviso;

import java.util.*;

/**
 * The definition of a fragment of music to be played within a composition. A
 * pattern consists of a list of Notes and a length, being that this length can
 * be a range of possible values. Once a pattern is initialized, it chooses a
 * length among this range, and when it's executed, it produces a list of
 * MIDINotes according to the notes included in it, as well as the length chosen
 * at initialization.
 * @author Fernie Canto
 */
public class Pattern implements java.io.Serializable {
    private final String id;
    private final ArrayList<Note> notes;
    private final IntegerRange duration;
    
    /**
     * The builder class for the pattern. Patterns are created exclusively
     * through this class, and all its attributes must be set through the
     * adequate accessors.
     */
    public static class PatternBuilder {
        private String id;
        private IntegerRange duration;
        private final ArrayList<Note> notes = new ArrayList<>();

        public String getId() {
            return id;
        }

        /**
         * Sets an identification for the pattern. The id can be any string of
         * characters.
         * @param id The identification of the pattern.
         * @return The builder object
         */
        public PatternBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public IntegerRange getDuration() {
            return duration;
        }

        /**
         * Sets the range of lengths for the pattern, in ticks. Every time the
         * pattern is initialized before execution, its actual length is chosen
         * from this range.
         * @param duration
         * @return The builder object
         */
        public PatternBuilder setDuration(IntegerRange duration) {
            this.duration = duration;
            return this;
        }
        
        public ArrayList<Note> getNotes() {
            return this.notes;
        }
        
        /**
         * Adds a Note to the pattern.
         * @param note The Note to be added
         * @return The builder object
         */
        public PatternBuilder addNote(Note note) {
            this.notes.add(note);
            return this;
        }
        
        /**
         * Builds the Pattern object according to the settings defined in this
         * object.
         * @return The resulting Pattern object
         */
        public Pattern build() {
            return new Pattern(this);
        }
    }
    
    
    protected Pattern(PatternBuilder builder) {
        this.id = builder.getId();
        this.duration = builder.getDuration();
        this.notes = builder.getNotes();
    }
    
   /**
    * Gets the identification string of the pattern.
    * @return The identification string
    */
    public String getId() {
        return this.id;
    }

    /**
     * Creates a new execution for the pattern, with a length randomly selected
     * from its range.
     * @param random The Random object
     * @return 
     */
    public PatternExecution getNextExecution(Random random) {
        return new PatternExecution(this, this.duration.getValue(random));
    }
    
    public Iterator<Note> getNoteIterator() {
        return this.notes.iterator();
    }
}
