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
public abstract class Section implements java.io.Serializable {
    final private String id;
    final private int timeSignatureNumerator = 4;
    final private int timeSignatureDenominator = 4;
    final private int tempo;
    final private boolean interruptTracks;
    final private boolean verbose;
    
    final private ArrayList<Track> tracks;
    
    private int currentRealTimePosition = 0;
    private SectionEnd currentSectionEnd;
    
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
            return this.value.compareTo(o);
        }
        
        public int compareTo(SectionEnd compareEnd) {
            return this.compareTo(compareEnd.intValue());
        }
        
        public boolean endIsKnown() {
            return true;
        }
        
        public int intValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return this.value.toString();
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
            return false;
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
    public int getTempo() {
        return this.tempo;
    }
    
    /**
     * Get the upper part (numerator) of the Section's time signature.
     * @return Numerator
     */
    public int getTimeSignatureNumerator() {
        return this.timeSignatureNumerator;
    }
    
    /**
     * Get the lower part (denominator) of the Section's time signature.
     * @return Denominator
     */
    public int getTimeSignatureDenominator() {
        return this.timeSignatureDenominator;
    }
    
    public ArrayList<Track> getTracks() {
        return this.tracks;
    }
    
    public String[] getTrackIds() {
        String[] ids = new String[this.tracks.size()];
        int idx = 0;
        for (Track track : this.tracks) {
            ids[idx] = track.getId();
            idx++;
        }
        
        return ids;
    }

    public void initialize(Random random) throws ImprovisoException {
        currentRealTimePosition = 0;
        currentSectionEnd = this.getSectionEnd(random);
        this.tracks.forEach((track) -> {
            track.initialize(random);
        });
    }
    
    /**
     * Executes the Section, returning the list of all notes produced by all
     * Tracks of the Section. After the execution, the current position of the
     * Section is updated.
     * @param random
     * @return List of generated Notes
     * @throws improviso.ImprovisoException
     */
    public MIDINoteList execute(Random random) throws ImprovisoException {
        if (this.tracks.isEmpty()) {
            throw new ImprovisoException("Trying to execute section with no tracks");
        }
        
        this.initialize(random);
        MIDINoteList notes = new MIDINoteList();
        
        while(this.sectionNotFinished(currentSectionEnd)) {
            Track selectedTrack = this.selectNextTrack(currentSectionEnd);
            
            displayMessage("Executing " + selectedTrack.getId() + " @ " + selectedTrack.getCurrentPosition());
            
            notes.addAll(selectedTrack.execute(
                    random,
                    currentSectionEnd,
                    this.interruptTracks,
                    this.calculatePatternPosition()
            ));
            SectionEnd newEnd = this.processTrackMessage(selectedTrack);
            if (currentSectionEnd.compareTo(newEnd) == 1) {
                currentSectionEnd = newEnd;
            }
            
            displayMessage("  Executed " + selectedTrack.getId() + " now @ " + selectedTrack.getCurrentPosition());
            displayMessage("  Section @ " + this.getCurrentPosition() + ", end @ " + currentSectionEnd.toString());
        }
        
        return notes;
    }

    public MIDINoteList executeTicks(Random random, int ticks) throws ImprovisoException {
        if (this.tracks.isEmpty()) {
            throw new ImprovisoException("Trying to execute section with no tracks");
        }
        
        MIDINoteList notes = new MIDINoteList();
        SectionEnd newEnd = this.currentSectionEnd;
        
        for (Track track : this.tracks) {
            notes.addAll(track.executeTicks(
                random,
                this.currentSectionEnd,
                ticks,
                this.interruptTracks,
                this.calculatePatternPosition()
            ));
            SectionEnd currentTrackEnd = this.processTrackMessage(track);
            if (currentTrackEnd.compareTo(newEnd) == -1) {
                newEnd = currentTrackEnd;
            }
        }
        MIDINoteList newNotes = notes.trimNotesAfterEnd(newEnd);
        this.currentSectionEnd = newEnd;
        
        if (currentRealTimePosition + ticks > this.currentSectionEnd.intValue()) {
            currentRealTimePosition = this.currentSectionEnd.intValue();
        } else {
            currentRealTimePosition += ticks;
        }
        
        return newNotes;
    }
    
    public boolean isFinished() {
        return this.currentSectionEnd.endIsKnown() && this.currentSectionEnd.intValue() <= this.currentRealTimePosition; // ???
    }
    
    public int getCurrentRealTimePosition() {
    //    return this.currentSectionEnd.intValue();
        return currentRealTimePosition;
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
    
    abstract protected boolean calculatePatternPosition();
    
    abstract protected SectionEnd getSectionEnd(Random random) throws ImprovisoException;
    
    /**
     * Process and interpret the Message received by the executed Track, updating
     * its internal state.
     * @param track
     * @return 
     */
    protected abstract SectionEnd processTrackMessage(Track track);
    
    public abstract void accept(SectionVisitor visitor);
}
