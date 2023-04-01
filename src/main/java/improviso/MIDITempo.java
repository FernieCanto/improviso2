/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.Serializable;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author LENOVO G40
 */
public class MIDITempo extends MIDIEvent implements Serializable {
    private final int tempo;
    
    public MIDITempo(long start, int tempo) {
        super(start);
        this.tempo = tempo;
    }
    
    @Override
    public ArrayList<MidiEvent> getEvents(MIDITrackList trackList) throws InvalidMidiDataException {
        ArrayList<MidiEvent> events = new ArrayList<>();
        
        MetaMessage tempoMessage = new MetaMessage();
        int microseconds = (int)(60000000 / this.getTempo());
        byte data[] = new byte[3];
        data[0] = (byte)(microseconds >>> 16);
        data[1] = (byte)(microseconds >>> 8);
        data[2] = (byte)(microseconds);
        tempoMessage.setMessage(0x51, data, 3);
        events.add(new MidiEvent(tempoMessage, this.getStart()));
                
        return events;
    }
    
    public int getTempo() {
        return this.tempo;
    }
}
