package improviso;

import java.util.*;
import java.util.regex.*;

/**
 * The definition of a note within a pattern. When creating a note, it's
 * necessary to inform its pitch, the starting position, the length and the
 * velocity. All of those attributes can be defined as ranges so that, when
 * executed, the note can produce a different MIDINote each time.
 * The position and length of the note are all in relation to the pattern in
 * which it's inserted, considering that the pattern always starts at 0. Both
 * attributes can be specified as an absolute value, and in this case, when the
 * pattern is not executed to its full length, the notes will be cut short or
 * removed according to the pattern's maximum length. Aside from that, the two
 * attributes can be defined in proportion to the pattern's length, so that when
 * the pattern is shrinked or enlarged, the notes will move along with it.
 * @author Fernie Canto
 */
public class Note {
    final private static java.util.regex.Pattern NOTE_NAME_PATTERN = java.util.regex.Pattern.compile("^([A-G])([#b])?(-2|-1|\\d)$");
    private static HashMap<Character, Integer> noteMap;
    
    final private int pitch;
    final private int MIDITrack;
    
    final private NotePosition positionStart;
    final private NotePosition positionLength;
    
    final private IntegerRange velocity;
    
    final private double probability;
    
    final private IntegerRange transposition;
    
    /**
     * The builder class for the note. Notes are created exclusively through
     * this class, and all its attributes must be set through the adequate
     * setters.
     */
    public static class NoteBuilder {
        private int pitch;
        private int MIDITrack = 1;
        private NotePosition positionStart = NotePosition.createNotePosition(new IntegerRange(0, 0));
        private NotePosition positionLength = NotePosition.createNotePosition(new IntegerRange(60, 60));
    
        private IntegerRange velocity = new IntegerRange(100, 100, 100, 100);
        private double probability = 1.0;
        private IntegerRange transposition = new IntegerRange(0, 0, 0, 0);
        
        public int getPitch() {
            return pitch;
        }

        /**
         * Sets the base pitch of the note. The numerical values for the pitch
         * follow the MIDI standard, with 0 representing C-2 and 72 representing
         * C4. These values can be produced from the names of the notes through
         * usage of the interpretNoteName method. The actual pitch generated
         * by the note can be made to vary by using a transposition range,
         * defined in the setTransposition method.
         * @param pitch The numeric value of the base pitch
         * @return The builder object
         */
        public NoteBuilder setPitch(int pitch) {
            this.pitch = pitch;
            return this;
        }

        public int getMIDITrack() {
            return MIDITrack;
        }

        /**
         * Sets the number of the MIDI track this note should be placed in.
         * @param MIDITrack The number of the track
         * @return The builder object
         */
        public NoteBuilder setMIDITrack(int MIDITrack) {
            this.MIDITrack = MIDITrack;
            return this;
        }
        
        /**
         * Sets the absolute starting point for the note. The resulting number
         * will represent the moment the note will be played inside the pattern,
         * in ticks, starting at 0. Notes defined this way will be removed if
         * they lie beyond the maximum length of the pattern.
         * @param start The range of starting positions, in ticks
         * @return The builder object
         */
        public NoteBuilder setStart(IntegerRange start) {
            this.positionStart = NotePosition.createNotePosition(start);
            return this;
        }
        
        /**
         * Sets the relative starting point for the note. The resulting number
         * will reprensent the moment the note will be played in proportion to
         * the length of the pattern as it's executed. Notes defined this way
         * will always be executed, regardless of the pattern's maximum length.
         * @param relativeStart The range of starting positions in proportion
         * to the pattern's length.
         * @return The builder object
         */
        public NoteBuilder setStart(DoubleRange relativeStart) {
            this.positionStart = NotePosition.createNotePosition(relativeStart);
            return this;
        }
        
        public NotePosition getPositionStart() {
            return this.positionStart;
        }

        /**
         * Sets the absolute length for the note. The resulting number will
         * represent for how long this note will last, in ticks. Notes defined
         * this way may be cut short if they were to last beyond maximum length
         * of the pattern.
         * @param length The range of lengths, in ticks
         * @return The builder object
         */
        public NoteBuilder setLength(IntegerRange length) {
            this.positionLength = NotePosition.createNotePosition(length);
            return this;
        }

        /**
         * Sets the relative length for the note. The resulting number
         * will reprensent for how long this note will last in proportion to
         * the length of the pattern as it's executed. Notes defined this way
         * will never be cut short, regardless of the pattern's maximum length.
         * @param relativeLength The range of lengths in proportion to the
         * pattern's length.
         * @return The builder object
         */
        public NoteBuilder setLength(DoubleRange relativeLength) {
            this.positionLength = NotePosition.createNotePosition(relativeLength);
            return this;
        }
        
        public NotePosition getPositionLength() {
            return this.positionLength;
        }

        public IntegerRange getVelocity() {
            return velocity;
        }

        /**
         * Sets the velocity of the note. The resulting values must range
         * between 0 and 127, with 0 being absolutely quiet and 127 being the
         * loudest.
         * @param velocity The range of velocities
         * @return The builder object
         */
        public NoteBuilder setVelocity(IntegerRange velocity) {
            this.velocity = velocity;
            return this;
        }

        public double getProbability() {
            return probability;
        }

        /**
         * Sets a probability that this note will generate a MIDINote when it's
         * executed. This value must range from 0 to 1, with 0 meaning it never
         * generates a MIDINote, and 1 meaning it always generates a MIDINote. A
         * value of 0.5 means this will generate a MIDINote half the time.
         * @param probability The probability of generating a MIDINote
         * @return The builder object
         */
        public NoteBuilder setProbability(double probability) {
            this.probability = probability;
            return this;
        }

        public IntegerRange getTransposition() {
            return transposition;
        }

        /**
         * Serts a range of transpositions for the base pitch of the note. This
         * number represents the number of semitones the note will be transposed
         * up, for positive values, or down, for negative values.
         * @param transposition The range of transpositions
         * @return The builder object
         */
        public NoteBuilder setTransposition(IntegerRange transposition) {
            this.transposition = transposition;
            return this;
        }
        
        /**
         * Builds a Note according to the settings defined in this object.
         * @return The resulting Note object
         */
        public Note build()
        {
            return new Note(this);
        }
    }
    
    abstract private static class NotePosition {
        public static NotePosition createNotePosition(IntegerRange range) {
            return new FixedNotePosition(range);
        }
        
        public static NotePosition createNotePosition(DoubleRange range) {
            return new RelativeNotePosition(range);
        }
        
        abstract public Integer calculate(Random random, Double position, Integer patternLength);
        abstract public NumberRange getRange();
    }
    
    private static class FixedNotePosition extends NotePosition {
        final private IntegerRange range;
        
        protected FixedNotePosition(IntegerRange range) {
            this.range = range;
        }
        
        @Override
        public Integer calculate(Random random, Double position, Integer patternLength) {
            return this.range.getValue(random, position);
        }
        
        @Override
        public IntegerRange getRange() {
            return this.range;
        }
    }
    
    private static class RelativeNotePosition extends NotePosition {
        final private DoubleRange range;
        
        protected RelativeNotePosition(DoubleRange range) {
            this.range = range;
        }
        
        @Override
        public Integer calculate(Random random, Double position, Integer patternLength) {
            return (int)(this.range.getValue(random, position) * patternLength);
        }
        
        @Override
        public DoubleRange getRange() {
            return this.range;
        }
    }
    
    protected Note(NoteBuilder builder) {
        this.pitch = builder.getPitch();
        this.MIDITrack = builder.getMIDITrack();
        this.positionStart = builder.getPositionStart();
        this.positionLength = builder.getPositionLength();
        this.velocity = builder.getVelocity();
        this.probability = builder.getProbability();
        this.transposition = builder.getTransposition();
    }
    
    private static HashMap<Character, Integer> getNoteNumberMap() {
        if (Note.noteMap == null) {
            noteMap = new HashMap<>();
            noteMap.put('C', 0);
            noteMap.put('D', 2);
            noteMap.put('E', 4);
            noteMap.put('F', 5);
            noteMap.put('G', 7);
            noteMap.put('A', 9);
            noteMap.put('B', 11);
        }
        return noteMap;
    }
    
    /**
     * Produces the MIDI note number corresponding to the note name. Note names
     * have to include a letter from A to G, an optional accidental ("b" for
     * flat or "#" for sharp) and the octave number. Additionally, the note name
     * can be an alias included in the composition file.
     * @param library A library of elements
     * @param stringNoteName The note name to be interpreted
     * @return The numerical value of the note
     * @throws ImprovisoException 
     */
    public static int interpretNoteName(ElementLibrary library, String stringNoteName)
        throws ImprovisoException {
        Matcher noteMatcher = NOTE_NAME_PATTERN.matcher(stringNoteName);
        
        if(library.hasNoteAlias(stringNoteName)) {
            return library.getNoteAlias(stringNoteName);
        } else if(noteMatcher.matches()) {
            int note = Note.getNoteNumberMap().get(noteMatcher.group(1).charAt(0));
            if(noteMatcher.group(2) != null) {
                if(noteMatcher.group(2).equals("b"))
                    note--;
                else
                    note++;
            }

            if(!noteMatcher.group(3).equals("-2")) {
                int octave = Integer.parseInt(noteMatcher.group(3));
                note += (octave+2) * 12;
            }
            return note;
        } else {
            try {
                return Integer.parseInt(stringNoteName);
            } catch(NumberFormatException e) {
                ImprovisoException exception = new ImprovisoException("Invalid note name: "+stringNoteName);
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }
    
    /**
     * Produces the pitch of the MIDINote after the application of the
     * transposition.
     * @param random The random object
     * @return The final value of the pitch
     */
    private int getTransposedPitch(Random random) {
        return this.pitch + this.transposition.getValue(random);
    }

    /**
     * Generates a MIDINote according to the parameters of the note. Depending
     * on the probability and on the maximum length of the pattern, the
     * resulting MIDINoteList may be empty or contain exactly one MIDINote.
     * @param random The random object
     * @param patternLength The actual length of the pattern being executed
     * @param position The position of the pattern in relation to the section
     * that is being currently executed.
     * @param maximumLength The maximum length of the pattern, as defined by
     * the section. If this pattern is being cut short by the section, the note
     * will be cut short or not generated at all.
     * @return A list containing zero or one MIDINote.
     */
    public MIDINoteList execute(Random random, int patternLength, double position, int maximumLength) {
        if(random.nextDouble() > this.probability) {
            return new MIDINoteList();
        }
        
        int noteStart = this.positionStart.calculate(random, position, patternLength);
        if (noteStart > maximumLength) {
            return new MIDINoteList();
        }
        
        int noteLength = this.positionLength.calculate(random, position, patternLength);
        if(noteStart + noteLength > maximumLength) {
            noteLength = maximumLength - noteStart;
        }
        
        return new MIDINoteList(new MIDINote(
                getTransposedPitch(random),
                noteStart,
                noteLength,
                this.velocity.getValue(random, position),
                this.MIDITrack
        ));
    }
}
