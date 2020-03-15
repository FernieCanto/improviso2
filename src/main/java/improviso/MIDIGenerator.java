/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

/**
 *
 * @author fernando
 */
public class MIDIGenerator implements MIDIGeneratorInterface {
    final private Sequence sequence;
    final private MidiDevice midiDevice;
    private MIDITrackList MIDITracks;
    private javax.sound.midi.Track[] tracks;
    
    private boolean interrupt = false;
    
    public MIDIGenerator(MidiDevice midiDevice) throws InvalidMidiDataException {
        this.midiDevice = midiDevice;
        sequence = new Sequence(Sequence.PPQ, 120);
    }
    
    @Override
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

    @Override
    public void setTempo(int tempo, long tick) throws InvalidMidiDataException {
        MetaMessage tempoMessage = new MetaMessage();
        int microseconds = (int)(60000000 / tempo);
        byte data[] = new byte[3];
        data[0] = (byte)(microseconds >>> 16);
        data[1] = (byte)(microseconds >>> 8);
        data[2] = (byte)(microseconds);
        tempoMessage.setMessage(0x51, data, 3);
        tracks[0].add(new MidiEvent(tempoMessage, tick));
    }

    @Override
    public void setTimeSignature(int numerator, int denominator, long tick) throws InvalidMidiDataException {
        MetaMessage signatureMessage = new MetaMessage();
        int denominatorExp = (int) (Math.log((double)denominator) / Math.log(2.0));
        byte data[] = new byte[4];
        data[0] = (byte)numerator;
        data[1] = (byte)denominatorExp;
        data[2] = (byte)24;
        data[3] = (byte)8;
        signatureMessage.setMessage(0x58, data, 4);
        tracks[0].add(new MidiEvent(signatureMessage, tick));
    }

    public void addNotes(MIDINoteList notes) throws InvalidMidiDataException {
        for(MIDIEvent midiEvent : notes) {
            MIDINote note = (MIDINote)midiEvent;
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
            
            try {
                Thread.sleep((sequencer.getMicrosecondLength() / 1000) + 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MIDIGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

            sequencer.stop();
            this.midiDevice.close();
        } catch(MidiUnavailableException ex) {
            System.out.println("MIDI sequencer not available: " + ex.getLocalizedMessage());
        } catch(InvalidMidiDataException ex) {
            System.out.println("MIDI data invalid: " + ex.getLocalizedMessage());
        }
    }
    
    public synchronized void playSequenceRealTime() throws InterruptedException {
        double qLength = (60.0d / 120.0d);
        double tickLength = (qLength / 120.0d);
        long currentPlayTick = 0;
        
        interrupt = false;
        
        try {
            int[] nextEvent = new int[this.sequence.getTracks().length];
            for (int idx = 0; idx < this.sequence.getTracks().length; idx++) {
                nextEvent[idx] = 0;
            }
            
            this.midiDevice.open();
            Receiver receiver = this.midiDevice.getReceiver();
            
            boolean finished;
            do {
                System.out.println("Beginning of loop: " + Instant.now().getNano());
                finished = true;
                int trackIdx = 0;
                for (javax.sound.midi.Track t : this.sequence.getTracks()) {
                    while (nextEvent[trackIdx] < t.size() && t.get(nextEvent[trackIdx]).getTick() == currentPlayTick) {
                        MidiEvent event = t.get(nextEvent[trackIdx]);
                        if (event.getMessage() instanceof MetaMessage && event.getMessage().getMessage()[1] == 0x51) {
                            tickLength = this.updateTempo((MetaMessage)event.getMessage());
                        } else {
                            receiver.send(event.getMessage(), 0);
                        }
                        nextEvent[trackIdx]++;
                    }
                    if(nextEvent[trackIdx] < t.size()) {
                        finished = false;
                    }

                    trackIdx++;
                }
                currentPlayTick++;
                
                long milliseconds = (long)Math.floor(tickLength * 1000.0d);
                double nanoseconds = ((tickLength * 1000.0d) - milliseconds) * 1000000d;
                
                System.out.println("End of loop: " + Instant.now().getNano());
                System.out.println("Nanoseconds to wait: " + ((milliseconds * 1000000) + nanoseconds));
                this.wait(milliseconds, (int)nanoseconds);
                System.out.println("After waiting: " + Instant.now().getNano());
            } while (!finished && !this.interrupt);
            this.midiDevice.close();
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(MIDIGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private double updateTempo(MetaMessage metaMessage) {
        int microsecondsPerQuarterNote = 
                (metaMessage.getMessage()[3] & 0xFF) << 16 |
                (metaMessage.getMessage()[4] & 0xFF) << 8 |
                (metaMessage.getMessage()[5] & 0xFF);
        System.out.println("Tempo: " + (60000000 / microsecondsPerQuarterNote) + "bpm");
        System.out.println(microsecondsPerQuarterNote);
        return ((double)microsecondsPerQuarterNote / 120.0d) / 1000000.0d;
    }
    
    public void setInterrupt() {
        this.interrupt = true;
    }

    public void generateFile(String fileName) throws IOException {
        java.io.File file = new java.io.File(fileName);
        MidiSystem.write(sequence, 1, file);
    }
}