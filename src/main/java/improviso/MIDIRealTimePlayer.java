/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.LinkedList;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

/**
 *
 * @author cfern
 */
public class MIDIRealTimePlayer {
    private final Composition composition;
    private final MidiDevice midiDevice;
    private final LinkedList<MidiEvent> events;
    private final MIDITrackList midiTrackList;
    
    private double tickLengthInMicrosseconds;
    private long ticksLastTempoChange;
    private long momentLastTempoChange;
    private long lastTickExecuted = 0;
    
    public MIDIRealTimePlayer(Composition composition, MidiDevice midiDevice) {
        this.composition = composition;
        this.midiDevice = midiDevice;
        this.events = new LinkedList<>();
        this.midiTrackList = composition.getMIDITrackList();
        
        this.tickLengthInMicrosseconds = 500000.0d / 120.0d;
        this.ticksLastTempoChange = 0;
        this.momentLastTempoChange = 0;
        this.lastTickExecuted = 0;
    }
    
    public void setMIDITracks() throws InvalidMidiDataException {
        for(MIDITrack track : midiTrackList) {
            MidiMessage instrumentMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, track.getChannel(), track.getInstrument(), 0);
            MidiEvent instrumentEvent = new MidiEvent(instrumentMessage, 0);
            events.add(instrumentEvent);
        }
    }

    public void addNotes(MIDINoteList notes) throws InvalidMidiDataException {
        for(MIDIEvent midiEvent : notes) {
            this.events.addAll(midiEvent.getEvents(midiTrackList));
        }
    }
    
    public void initialize() throws MidiUnavailableException, InvalidMidiDataException, ImprovisoException
    {
        this.midiDevice.open();
        this.momentLastTempoChange = this.midiDevice.getMicrosecondPosition();
        this.setMIDITracks();
        this.composition.initialize(true);
    }
    
    public boolean play() throws MidiUnavailableException, ImprovisoException, InvalidMidiDataException
    {
        long position = this.midiDevice.getMicrosecondPosition();
        if (!this.composition.getIsFinished() && (this.tickPositionInMicroseconds(lastTickExecuted) - (position - this.momentLastTempoChange)) < 5000) {
            this.addNotes(this.composition.executeTicks(10));
            this.lastTickExecuted += 10;
            this.events.sort(new MIDIEventSorter());
        }
        
        while (!this.events.isEmpty() && ( tickPositionInMicroseconds(this.events.getFirst().getTick()) <= (position - this.momentLastTempoChange))) {
            MidiEvent event = this.events.getFirst();
            if (event.getMessage() instanceof MetaMessage && event.getMessage().getMessage()[1] == 0x51) {
                this.tickLengthInMicrosseconds = this.tickLengthInMicroseconds((MetaMessage)event.getMessage());
                this.ticksLastTempoChange = event.getTick();
                this.momentLastTempoChange = position;
            }
            this.midiDevice.getReceiver().send(event.getMessage(), 0);
            this.events.removeFirst();
        }
        
        return !this.composition.getIsFinished() || !this.events.isEmpty();
    }
    
    public double tickPositionInMicroseconds(long tick) {
        return ( (tick - this.ticksLastTempoChange) * this.tickLengthInMicrosseconds);
    }
    
    private double tickLengthInMicroseconds(MetaMessage metaMessage) {
        int microsecondsPerQuarterNote = 
                (metaMessage.getMessage()[3] & 0xFF) << 16 |
                (metaMessage.getMessage()[4] & 0xFF) << 8 |
                (metaMessage.getMessage()[5] & 0xFF);
        return ((double)microsecondsPerQuarterNote / 120.0d);
    }
    
    public void closeDevice() throws MidiUnavailableException {
        this.midiDevice.getReceiver().close();
        this.midiDevice.close();
    }
}