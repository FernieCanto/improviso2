/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import javax.sound.midi.InvalidMidiDataException;

/**
 *
 * @author cfern
 */
public interface MIDIGeneratorInterface {
    public void setMIDITracks(MIDITrackList MIDITracks) throws InvalidMidiDataException;
    public void addNotes(MIDINoteList notes) throws InvalidMidiDataException;
    public void setTempo(int tempo, long tick) throws InvalidMidiDataException;
    public void setTimeSignature(int numerator, int denominator, long tick) throws InvalidMidiDataException;
}
