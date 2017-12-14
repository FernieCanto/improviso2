/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;

/**
 *
 * @author fernando
 */
public class MIDIGenerator {
    private ArrayList<MIDITrack> MIDITracks;
    private Sequence sequence;
    private javax.sound.midi.Track[] tracks;
    
    private long currentTick;
    
    public MIDIGenerator() throws InvalidMidiDataException {
        sequence = new Sequence(Sequence.PPQ, 120);
        currentTick = 0;
    }
  
    public MIDIGenerator(ArrayList<MIDITrack> MIDITracks) throws InvalidMidiDataException {
        sequence = new Sequence(Sequence.PPQ, 120);
        currentTick = 0;
        this.setMIDITracks(MIDITracks);
    }
    
    public void setMIDITracks(ArrayList<MIDITrack> MIDITracks) throws InvalidMidiDataException {
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
        try (Sequencer sequencer = MidiSystem.getSequencer()) {
            sequencer.open();
            sequencer.setSequence(this.sequence);
            sequencer.start();

            try {
                Thread.sleep((sequencer.getMicrosecondLength() / 1000) + 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MIDIGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

            sequencer.stop();
            sequencer.close();
        } catch(MidiUnavailableException ex) {
            System.out.println("MIDI sequencer not available: " + ex.getLocalizedMessage());
        } catch(InvalidMidiDataException ex) {
            System.out.println("MIDI data invalid: " + ex.getLocalizedMessage());
        }
    }

    public void generateFile(String fileName) throws IOException {
        java.io.File file = new java.io.File(fileName);
        MidiSystem.write(sequence, 1, file);
    }
}