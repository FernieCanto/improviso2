/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

/**
 *
 * @author LENOVO G40
 */
public class MIDIEvent {
    final private long start;
    
    public static MIDIEvent createEventWithOffset(MIDIEvent event, long offset) {
        if (event.getClass() == MIDINote.class) {
            return MIDIEvent.createEventWithOffset((MIDINote)event, offset);
        } else {
            return MIDIEvent.createEventWithOffset((MIDITempo)event, offset);
        }
    }
    
    public static MIDIEvent createEventWithOffset(MIDINote note, long offset) {
        return new MIDINote(note.getPitch(), note.getStart() + offset, note.getLength(), note.getVelocity(), note.getMIDITrack());
    }
    
    public static MIDIEvent createEventWithOffset(MIDITempo tempo, long offset) {
        return new MIDITempo(tempo.getStart() + offset, tempo.getTempo());
    }
    
    public MIDIEvent(long start) {
        this.start = start;
    }

    /**
     * Gets the starting position of the note in ticks.
     * @return The starting position
     */
    public long getStart() {
        return start;
    }
}
