/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.util.ArrayList;
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
public class MIDIRealTimePlayer implements MIDIGeneratorInterface {
    private final MidiDevice midiDevice;
    private final ArrayList<MidiEvent> events;
    private final int[] trackChannels;
    
    private double tickLengthInMicrosseconds;
    private long ticksLastTempoChange;
    private long momentLastTempoChange;
    private int eventCounter = 0;
    private long lastTickExecuted = 0;
    
    public MIDIRealTimePlayer(MidiDevice midiDevice) {
        this.midiDevice = midiDevice;
        this.events = new ArrayList<>();
        this.trackChannels = new int[16];
        
        this.tickLengthInMicrosseconds = 500000.0d / 120.0d;
        this.ticksLastTempoChange = 0;
        this.momentLastTempoChange = 0;
        this.lastTickExecuted = 0;
    }
    
    @Override
    public void setMIDITracks(MIDITrackList MIDITracks) throws InvalidMidiDataException {
        int trackIndex = 0;
        for(MIDITrack track : MIDITracks) {
            this.trackChannels[trackIndex] = track.getChannel();
            trackIndex++;
            
            MidiMessage instrumentMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, track.getChannel(), track.getInstrument(), 0);
            MidiEvent instrumentEvent = new MidiEvent(instrumentMessage, 0);
            events.add(instrumentEvent);
        }
    }

    @Override
    public void addNotes(MIDINoteList notes) throws InvalidMidiDataException {
        for(MIDIEvent midiEvent : notes) {
            MIDINote note = (MIDINote)midiEvent;
            //System.out.println("Note "+note.getPitch()+", start: "+note.getStart()+", length: "+note.getLength());
            int indexTrack = note.getMIDITrack() - 1;
            MidiEvent event;
            
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, this.trackChannels[indexTrack], note.getPitch(), note.getVelocity());
            event = new MidiEvent(noteMessage, note.getStart());
            events.add(event);

            noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_OFF, this.trackChannels[indexTrack], note.getPitch(), note.getVelocity());
            event = new MidiEvent(noteMessage, note.getStart() + note.getLength());
            events.add(event);
        }
    }

    @Override
    public void setTempo(int tempo, long tick) throws InvalidMidiDataException {
        System.out.println("ADDING TEMPO "+tempo+" to " + tick);
        MetaMessage tempoMessage = new MetaMessage();
        int microseconds = (int)(60000000 / tempo);
        byte data[] = new byte[3];
        data[0] = (byte)(microseconds >>> 16);
        data[1] = (byte)(microseconds >>> 8);
        data[2] = (byte)(microseconds);
        tempoMessage.setMessage(0x51, data, 3);
        events.add(new MidiEvent(tempoMessage, tick));
    }
    
    public void initialize(Composition composition) throws MidiUnavailableException, InvalidMidiDataException, ImprovisoException
    {
        this.midiDevice.open();
        this.momentLastTempoChange = this.midiDevice.getMicrosecondPosition();
        this.eventCounter = 0;
        composition.initialize(this);
    }
    
    public boolean play(Composition composition) throws MidiUnavailableException, ImprovisoException, InvalidMidiDataException
    {
        long position = this.midiDevice.getMicrosecondPosition();
        if (!composition.getIsFinished() && (this.tickPositionInMicroseconds(lastTickExecuted) - (position - this.momentLastTempoChange)) < 5000) {
            System.out.println("ADDING 10");
            composition.executeTicks(this, 10);
            this.lastTickExecuted += 10;
        }
        this.events.sort(new MIDIEventSorter());
        
        while (this.eventCounter < this.events.size() && ( tickPositionInMicroseconds(this.events.get(this.eventCounter).getTick()) < (position - this.momentLastTempoChange))) {
            MidiEvent event = this.events.get(this.eventCounter);
            if (event.getMessage() instanceof MetaMessage && event.getMessage().getMessage()[1] == 0x51) {
                System.out.println("CHANGING TEMPO to " + getTempoFromMessage((MetaMessage)event.getMessage()));
                this.tickLengthInMicrosseconds = this.tickLengthInMicroseconds((MetaMessage)event.getMessage());
                this.ticksLastTempoChange = event.getTick();
                this.momentLastTempoChange = position;
            } else {
                this.midiDevice.getReceiver().send(event.getMessage(), 0);
            }
            this.eventCounter++;
        }
        
        return !composition.getIsFinished() || (this.eventCounter < this.events.size());
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
    
    private int getTempoFromMessage(MetaMessage metaMessage) {
        int microsecondsPerQuarterNote = 
                (metaMessage.getMessage()[3] & 0xFF) << 16 |
                (metaMessage.getMessage()[4] & 0xFF) << 8 |
                (metaMessage.getMessage()[5] & 0xFF);
        return (int)(60000000 / microsecondsPerQuarterNote);
    }

    @Override
    public void setTimeSignature(int numerator, int denominator, long tick) throws InvalidMidiDataException {
    }
    
    public void closeDevice() throws MidiUnavailableException {
        this.midiDevice.getReceiver().close();
        this.midiDevice.close();
    }
}