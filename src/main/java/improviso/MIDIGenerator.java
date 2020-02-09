/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import static java.lang.Math.round;
import java.time.Instant;
import static java.time.temporal.ChronoUnit.MICROS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

/**
 *
 * @author fernando
 */
public class MIDIGenerator {
    private MIDITrackList MIDITracks;
    private Sequence sequence;
    private javax.sound.midi.Track[] tracks;
    private MidiDevice midiDevice;
    
    private long currentTick;
    
    public MIDIGenerator(MidiDevice midiDevice) throws InvalidMidiDataException {
        this.midiDevice = midiDevice;
        sequence = new Sequence(Sequence.PPQ, 120);
        currentTick = 0;
    }
  
    public MIDIGenerator(MIDITrackList MIDITracks) throws InvalidMidiDataException {
        sequence = new Sequence(Sequence.PPQ, 120);
        currentTick = 0;
        this.setMIDITracks(MIDITracks);
    }
    
    public void setMIDITracks(MIDITrackList MIDITracks) throws InvalidMidiDataException {
        this.MIDITracks = MIDITracks;
        tracks = new javax.sound.midi.Track[MIDITracks.size()];
        int trackIndex = 0;
        for(MIDITrack track : MIDITracks) {
            MidiMessage instrumentMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, track.getChannel(), track.getInstrument(), 0);
            MidiEvent instrumentEvent = new MidiEvent(instrumentMessage, 0);
            tracks[trackIndex] = sequence.createTrack();
            tracks[trackIndex].add(instrumentEvent);
            trackIndex++;
        }
    }
  
    public void setCurrentTick(long tick) {
        currentTick = tick;
    }

    public void setTempo(int tempo) throws InvalidMidiDataException {
        MetaMessage tempoMessage = new MetaMessage();
        int microseconds = (int)(60000000 / tempo);
        byte data[] = new byte[3];
        data[0] = (byte)(microseconds >>> 16);
        data[1] = (byte)(microseconds >>> 8);
        data[2] = (byte)(microseconds);
        tempoMessage.setMessage(0x51, data, 3);
        tracks[0].add(new MidiEvent(tempoMessage, currentTick));
    }

    public void setTimeSignature(int numerator, int denominator) throws InvalidMidiDataException {
        MetaMessage signatureMessage = new MetaMessage();
        int denominatorExp = (int) (Math.log((double)denominator) / Math.log(2.0));
        byte data[] = new byte[4];
        data[0] = (byte)numerator;
        data[1] = (byte)denominatorExp;
        data[2] = (byte)24;
        data[3] = (byte)8;
        signatureMessage.setMessage(0x58, data, 4);
        tracks[0].add(new MidiEvent(signatureMessage, currentTick));
    }

    public void addNotes(MIDINoteList notes) throws InvalidMidiDataException {
        for(MIDINote note : notes) {
            int indexTrack = note.getMIDITrack() - 1;
            MidiEvent event;
            
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, MIDITracks.get(indexTrack).getChannel(), note.getPitch(), note.getVelocity());
            event = new MidiEvent(noteMessage, note.getStart());
            tracks[indexTrack].add(event);

            noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_OFF, MIDITracks.get(indexTrack).getChannel(), note.getPitch(), note.getVelocity());
            event = new MidiEvent(noteMessage, note.getStart() + note.getLength());
            tracks[indexTrack].add(event);
        }
    }
    
    public void play() {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            this.midiDevice.open();
            Receiver receiver = this.midiDevice.getReceiver();

            Transmitter transmitter = sequencer.getTransmitter();
            transmitter.setReceiver(receiver);
            
            sequencer.open();
            sequencer.setSequence(this.sequence);
            sequencer.start();

            System.out.println(sequencer.getMicrosecondLength());
            
            System.out.println("INICIO");
            try {
                Thread.sleep((sequencer.getMicrosecondLength() / 1000) + 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MIDIGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("FIM");

            sequencer.stop();
            this.midiDevice.close();
        } catch(MidiUnavailableException ex) {
            System.out.println("MIDI sequencer not available: " + ex.getLocalizedMessage());
        } catch(InvalidMidiDataException ex) {
            System.out.println("MIDI data invalid: " + ex.getLocalizedMessage());
        }
    }
    
    public void playSequenceRealTime() {
        int tempo = 120;
        double qLength = (60.0d / (double)tempo);
        double tickLength = (qLength / 120.0d);
        System.out.println(qLength);
        System.out.println(tickLength);
        
        try {
            int[] nextEvent = new int[this.sequence.getTracks().length];
            for (int idx = 0; idx < this.sequence.getTracks().length; idx++) {
                nextEvent[idx] = 0;
            }
            
            this.midiDevice.open();
            Receiver receiver = this.midiDevice.getReceiver();
            
            Instant startingInstant = Instant.now();
            long initialDevicePosition = this.midiDevice.getMicrosecondPosition();
            
            boolean finished;
            do {
                finished = true;
                int trackIdx = 0;
                for (javax.sound.midi.Track t : this.sequence.getTracks()) {
                    if (nextEvent[trackIdx] < t.size()) {
                        MidiEvent evento = t.get(nextEvent[trackIdx]);
                        long eventTimeInMicroseconds = round(tickLength * (double)evento.getTick() * 1000000.0d);
                        
                        long currentPosition = startingInstant.until(Instant.now(), MICROS);
                        
                        System.out.println("Device time: " + this.midiDevice.getMicrosecondPosition());
                        System.out.println("Curent time: " + currentPosition);
                        System.out.println("Event time: " + eventTimeInMicroseconds);
                        
                        if((this.midiDevice.getMicrosecondPosition() - initialDevicePosition) >= eventTimeInMicroseconds) {
                            receiver.send(evento.getMessage(), 0);
                            nextEvent[trackIdx]++;
                        }
                        finished = false;
                    }

                    trackIdx++;
                }
            } while (!finished);
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(MIDIGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateFile(String fileName) throws IOException {
        java.io.File file = new java.io.File(fileName);
        MidiSystem.write(sequence, 1, file);
    }
}