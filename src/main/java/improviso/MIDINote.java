package improviso;

import java.io.Serializable;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * An instance of a note that's to be played in the composition. In contrast to
 * a Note, a MIDINote is an actual sound that's going to be heard, with a
 * specific starting position, length, pitch and velocity.
 * @author Fernie Canto
 */
public class MIDINote extends MIDIEvent implements Serializable {
    final private int pitch;
    final private int length;
    final private int velocity;
    final private int MIDITrack;
    
    /**
     * Main constructor for a MIDINote, with the specific values for the
     * attributes.
     * @param pitch The numeric value of the pitch, according to the MIDI
     * standard.
     * @param start The starting position of the note in ticks.
     * @param length The length of the note in ticks.
     * @param velocity The velocity of the note.
     * @param track The number of the MIDI track where this note will be placed.
     */
    public MIDINote(int pitch, long start, int length, int velocity, int track) {
        super(start);
        this.pitch = pitch;
        this.length = length;
        this.velocity = velocity;
        this.MIDITrack = track;
    }
    
    /**
     * Creates a copy of a note, with the starting position offset by a given
     * number of ticks.
     * @param anotherNote The note which must be copied
     * @param offset The number of ticks by which the note's starting position
     * will be offset
     */
    public MIDINote(MIDINote anotherNote, long offset) {
        super(anotherNote.getStart() + offset);
        this.pitch = anotherNote.getPitch();
        this.length = anotherNote.getLength();
        this.velocity = anotherNote.getVelocity();
        this.MIDITrack = anotherNote.getMIDITrack();
    }
    
    @Override
    public ArrayList<MidiEvent> getEvents(MIDITrackList trackList) throws InvalidMidiDataException {
        ArrayList<MidiEvent> events = new ArrayList<>();
        
        ShortMessage noteMessage = new ShortMessage();
        noteMessage.setMessage(ShortMessage.NOTE_ON, trackList.getChannel(this.getMIDITrack()), this.getPitch(), this.getVelocity());
        events.add(new MidiEvent(noteMessage, this.getStart()));

        noteMessage = new ShortMessage();
        noteMessage.setMessage(ShortMessage.NOTE_OFF, trackList.getChannel(this.getMIDITrack()), this.getPitch(), this.getVelocity());
        events.add(new MidiEvent(noteMessage, this.getStart() + this.getLength()));
                
        return events;
    }
    
    /**
     * Generates a textual description of the note.
     * @return The description
     */
    @Override
    public String toString() {
        return this.getPitch()+" at "+this.getStart()+", length "+this.getLength();
    }

    /**
     * Gets the numerical value of the pitch of the note, from 0 to 127,
     * according to the MIDI standard.
     * @return The numerical value of the pitch
     */
    public int getPitch() {
        return pitch;
    }

    /**
     * Gets the length of the note in ticks.
     * @return The length of the note
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the velocity of the note, from 0 to 127.
     * @return The velocity of the note
     */
    public int getVelocity() {
        return velocity;
    }

    /**
     * Gets the number of the MIDI track in which the note will be placed.
     * @return The number of the track
     */
    public int getMIDITrack() {
        return MIDITrack;
    }
}
