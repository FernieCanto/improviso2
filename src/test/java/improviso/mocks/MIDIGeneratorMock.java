/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package improviso.mocks;

import improviso.*;
import java.util.ArrayList;
import javax.sound.midi.InvalidMidiDataException;

/**
 *
 * @author User
 */
public class MIDIGeneratorMock extends MIDIGenerator {
    private ArrayList<MIDITrack> resultingMIDITracks;
    final private ArrayList<Long> currentTicks;
    final private ArrayList<Integer> tempos;
    final private ArrayList<Integer> timeSignatureNumerators;
    final private ArrayList<Integer> timeSignatureDenominators;
    final private ArrayList<MIDINoteList> noteLists;
    
    public MIDIGeneratorMock() throws InvalidMidiDataException {
        currentTicks = new ArrayList<>();
        tempos = new ArrayList<>();
        timeSignatureNumerators = new ArrayList<>();
        timeSignatureDenominators = new ArrayList<>();
        noteLists = new ArrayList<>();
    }
    
    public MIDIGeneratorMock(ArrayList<MIDITrack> tracks) throws InvalidMidiDataException {
        currentTicks = new ArrayList<>();
        tempos = new ArrayList<>();
        timeSignatureNumerators = new ArrayList<>();
        timeSignatureDenominators = new ArrayList<>();
        noteLists = new ArrayList<>();
    }
    
    @Override
    public void setMIDITracks(ArrayList<MIDITrack> MIDITracks) throws InvalidMidiDataException {
        this.resultingMIDITracks = MIDITracks;
    }
  
    @Override
    public void setCurrentTick(long tick) {
        currentTicks.add(tick);
    }

    @Override
    public void setTempo(int tempo) {
        this.tempos.add(tempo);
    }

    @Override
    public void setTimeSignature(int numerator, int denominator) {
        this.timeSignatureNumerators.add(numerator);
        this.timeSignatureDenominators.add(denominator);
    }

    @Override
    public void addNotes(MIDINoteList notes) {
        this.noteLists.add(notes);
    }
    
    @Override
    public void play() {
    }

    @Override
    public void generateFile(String fileName) {
    }
    
    public ArrayList<MIDITrack> getResultingMIDITracks() {
        return this.resultingMIDITracks;
    }

    public ArrayList<Long> getTicks() {
        return currentTicks;
    }

    public ArrayList<Integer> getTempos() {
        return tempos;
    }

    public ArrayList<Integer> getTimeSignatureNumerators() {
        return timeSignatureNumerators;
    }

    public ArrayList<Integer> getTimeSignatureDenominators() {
        return timeSignatureDenominators;
    }

    public ArrayList<MIDINoteList> getNoteLists() {
        return noteLists;
    }
}
