package improviso;

import java.util.*;

/**
 * This class implements a generic, abstract Section, which is a temporal
 * division of a Composition. A Section has a determined beginning and an ending,
 * though its ending may not be initially known, depending on the concrete
 * Section being used. Sections contain Tracks, which are executed simultaneously.
 * When the section is executed, it returns the sum of all notes produced by
 * its Tracks. Sections are directly subordinated to a Composition.
 * @author Fernie Canto
 */
public abstract class Section implements ExecutableSection, java.io.Serializable {
    final private String id;
    final private int timeSignatureNumerator = 4;
    final private int timeSignatureDenominator = 4;
    final private int tempo;
    final private boolean interruptTracks;
    final private boolean verbose;
    
    final private ArrayList<Track> tracks;
    
    public abstract static class SectionBuilder {
        private String id;
        private Integer tempo = 120;
        private final ArrayList<Track> tracks = new ArrayList<>();
        private boolean interruptTracks = false;
        private boolean verbose = false;
        
        public String getId() {
            return this.id;
        }
        
        public SectionBuilder setId(String id) {
            this.id = id;
            return this;
        }
        
        public Integer getTempo() {
            return this.tempo;
        }
        
        public SectionBuilder setTempo(Integer tempo) {
            this.tempo = tempo;
            return this;
        }
        
        public boolean getInterruptTracks() {
            return this.interruptTracks;
        }
        
        public SectionBuilder setInterruptTracks(boolean interrupt) {
            this.interruptTracks = interrupt;
            return this;
        }
        
        public ArrayList<Track> getTracks() {
            return this.tracks;
        }
        
        public SectionBuilder addTrack(Track track) {
            this.tracks.add(track);
            return this;
        }
        
        public boolean getVerbose() {
            return this.verbose;
        }
        
        public void verbose() {
            this.verbose = true;
        }
        
        abstract public Section build();
    }
    
    public static class SectionEnd implements Comparable<Integer>, java.io.Serializable {
        private final Integer value;
        
        public static SectionEnd createEnd(Integer value) {
            if (value == null) {
                return new UnknownSectionEnd();
            } else {
                return new SectionEnd(value);
            }
        }
        
        private SectionEnd(Integer value) {
            this.value = value;
        }

        @Override
        public int compareTo(Integer o) {
            if (!this.endIsKnown()) {
                return 1;
            } else {
                return this.value.compareTo(o);
            }
        }
        
        public int compareTo(SectionEnd compareEnd) {
            if (compareEnd.endIsKnown()) {
                return this.compareTo(compareEnd.intValue());
            } else {
                if (!this.endIsKnown()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        }
        
        public boolean endIsKnown() {
            return this.value != null;
        }
        
        public int intValue() {
            if (this.endIsKnown()) {
                return this.value;
            } else {
                return Integer.MAX_VALUE;
            }
        }
        
        @Override
        public String toString() {
            if (this.endIsKnown()) {
                return this.value.toString();
            } else {
                return "unknown";
            }
        }
    }
    
    public static class UnknownSectionEnd extends SectionEnd {
        public UnknownSectionEnd() {
            super(null);
        }
        
        @Override
        public int compareTo(Integer o) {
            return 1;
        }
        
        @Override
        public int compareTo(SectionEnd compareEnd) {
            if (compareEnd.endIsKnown()) {
                return 1;
            } else {
                return 0;
            }
        }
        
        @Override
        public boolean endIsKnown() {
            return true;
        }
        
        @Override
        public int intValue() {
            return Integer.MAX_VALUE;
        }
        
        @Override
        public String toString() {
            return "unknown";
        }
    }
    
    protected Section(SectionBuilder builder) {
        this.id = builder.getId();
        this.tempo = builder.getTempo();
        this.interruptTracks = builder.getInterruptTracks();
        this.tracks = builder.getTracks();
        this.verbose = builder.getVerbose();
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    public boolean getInterruptTracks() {
        return this.interruptTracks;
    }
    
    /**
     * Return the tempo of the section.
     * @return Tempo in BPM
     */
    @Override
    public int getTempo() {
        return this.tempo;
    }
    
    /**
     * Get the upper part (numerator) of the Section's time signature.
     * @return Numerator
     */
    @Override
    public int getTimeSignatureNumerator() {
        return this.timeSignatureNumerator;
    }
    
    /**
     * Get the lower part (denominator) of the Section's time signature.
     * @return Denominator
     */
    @Override
    public int getTimeSignatureDenominator() {
        return this.timeSignatureDenominator;
    }
    
    @Override
    public ArrayList<Track> getTracks() {
        return this.tracks;
    }
    
    /**
     * Executes the Section, returning the list of all notes produced by all
     * Tracks of the Section. After the execution, the current position of the
     * Section is updated.
     * @param random
     * @return List of generated Notes
     * @throws improviso.ImprovisoException
     */
    @Override
    public MIDINoteList execute(Random random) throws ImprovisoException {
        if (this.tracks.isEmpty()) {
            throw new ImprovisoException("Trying to execute section with no tracks");
        }
        
        this.tracks.forEach((track) -> {
            track.initialize();
            track.selectNextPattern(random);
        });
        
        MIDINoteList notes = new MIDINoteList();
        SectionEnd end = this.initialize(random);
        
        while(this.sectionNotFinished(end)) {
            Track selectedTrack = this.selectNextTrack(end);
            
            displayMessage("Executing " + selectedTrack.getId() + " @ " + selectedTrack.getCurrentPosition());
            
            notes.addAll(selectedTrack.execute(
                    random,
                    end,
                    this.interruptTracks
            ));
            SectionEnd newEnd = this.processTrackMessage(selectedTrack);
            if (end.compareTo(newEnd) == 1) {
                end = newEnd;
            }
            
            displayMessage("  Executed " + selectedTrack.getId() + " now @ " + selectedTrack.getCurrentPosition());
            displayMessage("  Section @ " + this.getCurrentPosition() + ", end @ " + end.toString());

            selectedTrack.selectNextPattern(random);
        }
        
        return notes;
    }
    
    private boolean sectionNotFinished(SectionEnd end) {
        return end.compareTo(this.getCurrentPosition()) == 1;
    }

    private Track selectNextTrack(SectionEnd end) {
        Track selectedTrack = this.tracks.get(0);
        for(Track track : this.tracks) {
            if(end.compareTo(selectedTrack.getCurrentPosition()) <= 0 || track.getEnd() < selectedTrack.getEnd()) {
                selectedTrack = track;
            }
        }
        return selectedTrack;
    }
    
    private int getCurrentPosition() {
        Track selectedTrack = this.tracks.get(0);
        for(Track t : this.tracks) {
            if(t.getCurrentPosition() < selectedTrack.getCurrentPosition()) {
                selectedTrack = t;
            }
        }
        return selectedTrack.getCurrentPosition();
    }
    
    @Override
    public int getActualEnd() {
        Track selectedTrack = this.tracks.get(0);
        for(Track t : this.tracks) {
            if(t.getCurrentPosition() > selectedTrack.getCurrentPosition()) {
                selectedTrack = t;
            }
        }
        return selectedTrack.getCurrentPosition();
    }
    
    protected void displayMessage(String message) {
        if (this.verbose) {
            System.out.println(message);
        }
    }
    
    abstract protected SectionEnd initialize(Random random) throws ImprovisoException;
    
    /**
     * Process and interpret the Message received by the executed Track, updating
     * its internal state.
     * @param track
     * @return 
     */
    protected abstract SectionEnd processTrackMessage(Track track);
}
